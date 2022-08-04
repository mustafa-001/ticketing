package mutlu.ticketingapp.service;

import mutlu.ticketingapp.common.PassengerGender;
import mutlu.ticketingapp.common.PaymentResponse;
import mutlu.ticketingapp.common.UserType;
import mutlu.ticketingapp.config.PaymentClient;
import mutlu.ticketingapp.config.RabbitMQConfig;
import mutlu.ticketingapp.dto.*;
import mutlu.ticketingapp.dto.GetTripDto;
import mutlu.ticketingapp.dto.email_and_sms_service.TicketInformationMessageDto;
import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.repository.TicketRepository;
import mutlu.ticketingapp.repository.TripRepository;
import mutlu.ticketingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PaymentClient paymentClient;
    private final AmqpTemplate rabbitTemplate;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public TicketService(TicketRepository ticketRepository, TripRepository tripRepository, UserRepository userRepository, PaymentClient paymentClient, RabbitMQConfig rabbitMQConfig, AmqpTemplate rabbitTemplate) {
        this.ticketRepository = ticketRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.paymentClient = paymentClient;
        this.rabbitTemplate = rabbitTemplate;
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
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(request.clientPaymentInfoDto(), user.getUserId(), trip.getTripId(), trip.getPrice());
        log.info("Requesting payment from payment service with {}", paymentRequestDto);
        PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequestDto);
        if (paymentResponse == PaymentResponse.DENIED | paymentResponse == PaymentResponse.INCOMPLETE) {
            throw new RuntimeException("Payment could not be made.");
        }
        GetTicketDto ticketDto = GetTicketDto.fromTicket(ticket);
        TicketInformationMessageDto ticketInformationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), GetTripDto.fromTrip(trip), (long) 1);
        log.info("Payment successful, sending SMS request to message queue. {}", ticketInformationMessage);
        rabbitTemplate.convertAndSend("ticketing.sms", ticketInformationMessage);

        return ticketDto;
    }

    public List<GetTicketDto> getByUserId(Long userId) {
        log.info("Querying tickets by user with id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        return ticketRepository.findByUser(user).stream().map(GetTicketDto::fromTicket).toList();
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
        PaymentRequestDto paymentRequest = new PaymentRequestDto(requests.get(0).clientPaymentInfoDto(), user.getUserId(), trip.getTripId(),
                trip.getPrice().multiply(BigDecimal.valueOf(requests.size())));

        PaymentResponse paymentResponse = paymentClient.createPayment(paymentRequest);
        if (paymentResponse == PaymentResponse.DENIED | paymentResponse == PaymentResponse.INCOMPLETE) {
            throw new RuntimeException("Payment could not be made.");
        }
        TicketInformationMessageDto ticketInformationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), GetTripDto.fromTrip(trip), (long) getTicketDtos.size());
        rabbitTemplate.convertAndSend(ticketInformationMessage);
        return getTicketDtos;
    }

    public List<GetTripDto> search(SearchTripDto searchParameters) {
        log.info("Searching tickets for {}", searchParameters);
        return tripRepository.findByDepartureStationAndArrivalStationAndVehicleType(
                searchParameters.departureStation(),
                searchParameters.arrivalStation(),
                searchParameters.vehicleType()
        ).stream().filter(trip -> trip.getDeparture().toLocalDate().equals(searchParameters.date())).map(GetTripDto::fromTrip).toList();
    }
}
