package mutlu.ticketingapp.service;

import feign.RetryableException;
import mutlu.ticketingapp.common.PassengerGender;
import mutlu.ticketingapp.common.PaymentResponse;
import mutlu.ticketingapp.common.UserType;
import mutlu.ticketingapp.config.PaymentClient;
import mutlu.ticketingapp.config.RabbitMQConfig;
import mutlu.ticketingapp.dto.*;
import mutlu.ticketingapp.dto.email_and_sms_service.TicketInformationMessageDto;
import mutlu.ticketingapp.dto.user.GetUserDto;
import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.exception.NotEnoughTicketsLeftExceptionAbstract;
import mutlu.ticketingapp.exception.PaymentExceptionAbstract;
import mutlu.ticketingapp.exception.UserCannotBuyMoreTicketsExceptionAbstract;
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

    public ticket.GetTicketDto addTicket(ticket.CreateTicketDto request) {
        log.debug("Buying a ticket to tripId {} with userId {}", request.tripId(), request.userId());
        User user = userRepository.findById(request.userId()).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        Trip trip = tripRepository.findById(request.tripId()).orElseThrow(() -> {
            throw new IllegalArgumentException("Trip cannot be found.");
        });

        checkMaximumTicketLimits(user, trip, 1);

        if (!checkCapacity(trip, 1)) {
            throw new NotEnoughTicketsLeftExceptionAbstract();
        }

        Ticket ticket = new Ticket();
        ticket.setTrip(trip)
                .setPassengerGender(request.passengerGender())
                .setUser(user);

        ticket = ticketRepository.save(ticket);
        mutlu.ticketingapp.dto.ticket.PaymentRequestDto paymentRequestDto = new ticket.PaymentRequestDto(request.clientPaymentInfoDto(), user.getUserId(), trip.getTripId(), trip.getPrice());
        handlePayment(paymentRequestDto);
        mutlu.ticketingapp.dto.ticket.GetTicketDto ticketDto = mutlu.ticketingapp.dto.ticket.GetTicketDto.fromTicket(ticket);
        TicketInformationMessageDto ticketInformationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), mutlu.ticketingapp.dto.ticket.GetTripDto.fromTrip(trip), (long) 1);
        log.info("Payment successful, sending SMS request to message queue. {}", ticketInformationMessage);
        rabbitTemplate.convertAndSend("ticketing.sms", ticketInformationMessage);

        return ticketDto;
    }

    public List<ticket.GetTicketDto> getByUserId(Long userId) {
        log.info("Querying tickets by user with id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        return ticketRepository.findByUser(user).stream().map(ticket.GetTicketDto::fromTicket).toList();
    }

    private boolean checkCapacity(Trip trip, int ticketsRequestedInThisOrder) {
        return ticketRepository.findByTrip(trip).size() + ticketsRequestedInThisOrder <= trip.getVehicleType().getCapacity();
    }

    private void checkMaximumTicketLimits(User user, Trip trip, int ticketsRequestedInThisOrder) {
        int ticketsForThisTrip = ticketRepository.findByUserAndTrip(user, trip).size() + ticketsRequestedInThisOrder;
        if (user.getUserType().equals(UserType.INDIVIDUAL)) {
            if (ticketsForThisTrip == 5) {
                log.info("User: {} has bougth 5 ticket for Trip: {} ", user, trip);
                throw new UserCannotBuyMoreTicketsExceptionAbstract(user.getUserType());
            } else if (ticketsForThisTrip > 5) {
                throw new IllegalStateException();
            }
        } else if (user.getUserType().equals(UserType.CORPORATE)) {
            if (ticketsForThisTrip == 20) {
                log.info("User: {} has bougth 20 ticket for Trip: {} ", user, trip);
                throw new UserCannotBuyMoreTicketsExceptionAbstract(user.getUserType());
            } else if (ticketsForThisTrip > 20) {
                throw new IllegalStateException();
            }
        }
    }

    public List<ticket.GetTicketDto> addTicketBulk(List<ticket.CreateTicketDto> requests) {
        long malePassengerForThisOrder = requests.stream().filter(request -> request.passengerGender().equals(PassengerGender.MALE)).count();

        if (requests.stream().map(ticket.CreateTicketDto::userId).distinct().count() > 1) {
            log.debug("CreateTicketDto's have different userId fields");
            throw new IllegalArgumentException("CreateTicketDto's cannot have different userId fields when requesting bulk order.");
        }
        if (requests.stream().map(ticket.CreateTicketDto::tripId).distinct().count() > 1) {
            log.debug("CreateTicketDto's have different tripId fields");
            throw new IllegalArgumentException("CreateTicketDto's cannot have different tripId fields when requesting bulk order.");
        }

        User user = userRepository.findById(requests.get(0).userId()).orElseThrow(() -> {
            throw new IllegalArgumentException("User cannot be found.");
        });
        Trip trip = tripRepository.findById(requests.get(0).tripId()).orElseThrow(() -> {
            throw new IllegalArgumentException("Trip cannot be found.");
        });

        checkMaximumTicketLimits(user, trip, requests.size());

        if (!checkCapacity(trip, 1)) {
            throw new NotEnoughTicketsLeftExceptionAbstract();
        }

        if (malePassengerForThisOrder > 2) {
            throw new RuntimeException("An individual user can only buy 2 tickets for male passengers in a request.");
        }

        List<ticket.GetTicketDto> getTicketDtos = new ArrayList<>();
        for (var r : requests) {
            Ticket ticket = new Ticket();
            ticket.setTrip(trip)
                    .setPassengerGender(r.passengerGender())
                    .setUser(user);
            ticket = ticketRepository.save(ticket);
            getTicketDtos.add(mutlu.ticketingapp.dto.ticket.GetTicketDto.fromTicket(ticket));
        }
        ticket.PaymentRequestDto paymentRequest = new ticket.PaymentRequestDto(requests.get(0).clientPaymentInfoDto(), user.getUserId(), trip.getTripId(),
                trip.getPrice().multiply(BigDecimal.valueOf(requests.size())));

        handlePayment(paymentRequest);
        TicketInformationMessageDto ticketInformationMessage = new TicketInformationMessageDto(GetUserDto.fromUser(user), ticket.GetTripDto.fromTrip(trip), (long) getTicketDtos.size());
        rabbitTemplate.convertAndSend(ticketInformationMessage);
        return getTicketDtos;
    }

    private void handlePayment(ticket.PaymentRequestDto paymentRequest) {
        PaymentResponse paymentResponse = null;
        try {
            paymentResponse = paymentClient.createPayment(paymentRequest);
        } catch (RetryableException e) {
            throw new PaymentExceptionAbstract(PaymentResponse.CANNOT_CONNECT);
        }
        if (paymentResponse == PaymentResponse.DENIED | paymentResponse == PaymentResponse.INCOMPLETE | paymentResponse == null) {
            log.debug("Error when processing payment: {}", paymentRequest);
            throw new PaymentExceptionAbstract(paymentResponse);
        }

        log.info("Payment Request: {}, PaymentResponse: {}", paymentRequest, paymentResponse);
    }

    public List<ticket.GetTripDto> search(ticket.SearchTripDto searchParameters) {
        log.info("Searching tickets for {}", searchParameters);
        return tripRepository.findByDepartureStationAndArrivalStationAndVehicleType(
                searchParameters.departureStation(),
                searchParameters.arrivalStation(),
                searchParameters.vehicleType()
        ).stream().filter(trip -> trip.getDeparture().toLocalDate().equals(searchParameters.date())).map(ticket.GetTripDto::fromTrip).toList();
    }
}
