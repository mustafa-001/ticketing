package mutlu.ticketingapp.service;

import mutlu.ticketingapp.common.PassengerGender;
import mutlu.ticketingapp.common.UserType;
import mutlu.ticketingapp.dto.*;
import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.repository.TicketRepository;
import mutlu.ticketingapp.repository.TripRepository;
import mutlu.ticketingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public TicketService(TicketRepository ticketRepository, TripRepository tripRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public GetTicketDto addTicket(CreateTicketDto request) {
        log.info("Buying a ticket to tripId {} with userId {}", request.tripId(), request.userId());
        User user = userRepository.findById(request.userId()).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        Trip trip = tripRepository.findById(request.tripId()).orElseThrow(() -> {
            throw new IllegalArgumentException("Trip cannot be found.");
        });

        checkMaximumTicketLimits(user, trip, 1);

        if (!checkCapacity(trip, 1)) {
            throw new RuntimeException("This trip does not have enough unsold tickets.");
        }

        Ticket ticket = new Ticket();
        ticket.setTrip(trip)
                .setPassengerGender(request.passengerGender())
                .setUser(user);

        ticket = ticketRepository.save(ticket);
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(request.paymentDto(), trip.getPrice());
        log.info("Requesting payment from payment service with {}", paymentRequestDto);
        //TODO make payment.
        GetTicketDto ticketDto = GetTicketDto.fromTicket(ticket);
        TicketInformationMessageDto ticketInformationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), List.of(ticketDto));
        //TODO send information email.

        return ticketDto;

    }

    private boolean checkCapacity(Trip trip, int ticketsRequestedInThisOrder) {
        return ticketRepository.findByTrip(trip).size() + ticketsRequestedInThisOrder <= trip.getVehicleType().getCapacity();
    }

    private void checkMaximumTicketLimits(User user, Trip trip, int ticketsRequestedInThisOrder) {
        int ticketsForThisTrip = ticketRepository.findByUserAndTrip(user, trip).size() + ticketsRequestedInThisOrder;
        if (user.getUserType().equals(UserType.INDIVIDUAL)) {
            if (ticketsForThisTrip == 5) {
                log.info("User: {} has bougth 5 ticket for Trip: {} ", user, trip);
                throw new RuntimeException("An individual user cannot by more than 5 ticket.");
            } else if (ticketsForThisTrip > 5) {
                throw new IllegalStateException("A user cannot have more than 5 ticket for a trip.");
            }
        } else if (user.getUserType().equals(UserType.CORPORATE)) {
            if (ticketsForThisTrip == 20) {
                log.info("User: {} has bougth 20 ticket for Trip: {} ", user, trip);
                throw new RuntimeException("A corporate user cannot by more than 20 ticket for a trip.");
            } else if (ticketsForThisTrip > 20) {
                throw new IllegalStateException("A user cannot have more than 20 ticket for a trip.");
            }
        }
    }

    public List<GetTicketDto> addTicketBulk(List<CreateTicketDto> requests) {
        long malePassengerForThisOrder = requests.stream().filter(request -> request.passengerGender().equals(PassengerGender.MALE)).count();

        if (requests.stream().map(CreateTicketDto::userId).distinct().count() > 1) {
            log.info("CreateTicketDto's have different userId fields");
            throw new RuntimeException("CreateTicketDto's cannot have different userId fields when requesting bulk order.");
        }
        if (requests.stream().map(CreateTicketDto::tripId).distinct().count() > 1) {
            log.info("CreateTicketDto's have different tripId fields");
            throw new RuntimeException("CreateTicketDto's cannot have different tripId fields when requesting bulk order.");
        }

        User user = userRepository.findById(requests.get(0).userId()).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        Trip trip = tripRepository.findById(requests.get(0).tripId()).orElseThrow(() -> {
            throw new IllegalArgumentException("Trip cannot be found.");
        });

        checkMaximumTicketLimits(user, trip, requests.size());

        if (!checkCapacity(trip, 1)) {
            throw new RuntimeException("This trip does not have enough unsold tickets.");
        }

        if (malePassengerForThisOrder > 2) {
            throw new RuntimeException("An individual user can only buy 2 tickets for male passengers in a request.");
        }

        List<GetTicketDto> getTicketDtos = new ArrayList<>();
        for (var r : requests) {
            Ticket ticket = new Ticket();
            ticket.setTrip(trip)
                    .setPassengerGender(r.passengerGender())
                    .setUser(user);
            ticket = ticketRepository.save(ticket);

            getTicketDtos.add(GetTicketDto.fromTicket(ticket));
        }
        PaymentRequestDto paymentRequest = new PaymentRequestDto(requests.get(0).paymentDto(),
                trip.getPrice().multiply(BigDecimal.valueOf(requests.size())));
        //TODO make payment.

        TicketInformationMessageDto informationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), getTicketDtos);
        //TODO send information email.
        //TODO commit to database.
        return getTicketDtos;
    }

}
