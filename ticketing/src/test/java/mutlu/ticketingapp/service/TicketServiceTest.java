package mutlu.ticketingapp.service;

import mutlu.ticketingapp.config.PaymentClient;
import mutlu.ticketingapp.dto.email_and_sms_service.TicketInformationMessageDto;
import mutlu.ticketingapp.dto.ticket.ClientPaymentInfoDto;
import mutlu.ticketingapp.dto.ticket.CreateTicketDto;
import mutlu.ticketingapp.dto.ticket.GetTripDto;
import mutlu.ticketingapp.dto.ticket.SearchTripDto;
import mutlu.ticketingapp.entity.Ticket;
import mutlu.ticketingapp.entity.Trip;
import mutlu.ticketingapp.entity.User;
import mutlu.ticketingapp.enums.*;
import mutlu.ticketingapp.exception.CannotSellTicketsException;
import mutlu.ticketingapp.exception.NotEnoughTicketsLeftException;
import mutlu.ticketingapp.exception.UserCannotBuyMoreTicketsException;
import mutlu.ticketingapp.repository.TicketRepository;
import mutlu.ticketingapp.repository.TripRepository;
import mutlu.ticketingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private AmqpTemplate rabbitTemplate;

    private final CreateTicketDto createTicketDto =
            new CreateTicketDto(1L, 1L, PassengerGender.MALE,
                    new ClientPaymentInfoDto(PaymentType.CREDIT_CARD, "AABBCCDD"));
    private final User user = new User();
    private final Trip trip = new Trip();
    private List<CreateTicketDto> ticketDtoList;

    @BeforeEach
    void init() {
        user.setUserType(UserType.CORPORATE);
        user.setPhoneNumber("001122334455");
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        trip.setVehicleType(VehicleType.BUS);
        trip.setPrice(new BigDecimal(234));
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));

        Ticket ticket = new Ticket();
        ticket.setTrip(trip);
        ticket.setUser(user);
        ticket.setTicketId(1000L);
        ticket.setPassengerGender(PassengerGender.MALE);

        Mockito.when(ticketRepository.save(Mockito.any())).thenReturn(ticket);
        Mockito.when(ticketRepository.findByTrip(Mockito.any())).thenReturn(new ArrayList<>());
        Mockito.when(ticketRepository.findByUserAndTrip(Mockito.any(), Mockito.any()))
                .thenReturn(new ArrayList<>());

        CreateTicketDto createTicketDto1 =
                new CreateTicketDto(1L, 2L, PassengerGender.FEMALE,
                        new ClientPaymentInfoDto(PaymentType.CREDIT_CARD, "AABBCCDD"));

        CreateTicketDto createTicketDto2 =
                new CreateTicketDto(1L, 2L, PassengerGender.FEMALE, null);

        CreateTicketDto createTicketDto3 =
                new CreateTicketDto(1L, 2L, PassengerGender.MALE, null);
        ticketDtoList = List.of(createTicketDto1, createTicketDto2, createTicketDto3);
    }

    @Test
    void shouldThrowInvalidParameterExceptionWhenCalledWithInvalidUserId() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> ticketService.addTicket(createTicketDto));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowInvalidParameterExceptionWhenCalledWithInvalidTripId() {
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable exception = catchThrowable(() -> ticketService.addTicket(createTicketDto));

        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowUserCannotBuyMoreTicketsExceptionWhenPersonalUserAttemptsToBuyMoreThanAllowedLimit() {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            tickets.add(new Ticket());
        }

        Mockito.when(ticketRepository.findByUserAndTrip(Mockito.any(), Mockito.any()))
                .thenReturn(tickets);
        User user = new User();
        user.setUserType(UserType.INDIVIDUAL);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Throwable exception = catchThrowable(() -> ticketService.addTicket(createTicketDto));

        assertThat(exception).isInstanceOf(UserCannotBuyMoreTicketsException.class);
    }


    @Test
    void shouldThrowUserCannotBuyMoreTicketsExceptionWhenCorporateUserAttemptsToBuyMoreThanAllowedLimit() {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            tickets.add(new Ticket());
        }

        Mockito.when(ticketRepository.findByUserAndTrip(Mockito.any(), Mockito.any()))
                .thenReturn(tickets);
        User user = new User();
        user.setUserType(UserType.CORPORATE);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Throwable exception = catchThrowable(() -> ticketService.addTicket(createTicketDto));

        assertThat(exception).isInstanceOf(UserCannotBuyMoreTicketsException.class);
    }

    @Test
    void shouldThrowNotEnoughTicketsLeftExceptionWhenNotEnoughTicketsToSell() {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < VehicleType.BUS.getCapacity(); i++) {
            tickets.add(new Ticket());
        }
        Trip trip = new Trip();
        trip.setVehicleType(VehicleType.BUS);

        Mockito.when(ticketRepository.findByTrip(Mockito.any())).thenReturn(tickets);
        Mockito.when(ticketRepository.findByUserAndTrip(Mockito.any(), Mockito.any())).thenReturn(List.of());
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));

        Throwable exception = catchThrowable(() -> ticketService.addTicket(createTicketDto));

        assertThat(exception).isInstanceOf(NotEnoughTicketsLeftException.class);
    }

    @Test
    void shouldCallSaveOnRepositoryWhenChecksAreCorrect() {
        catchThrowable(() -> ticketService.addTicket(createTicketDto));

        verify(ticketRepository, times(1))
                .save(argThat(ticket -> ticket.getUser() == user));
    }

    @Test
    void shouldCallPaymentServiceWhenChecksAreCorrect() {
        trip.setPrice(new BigDecimal(111));
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));
        Mockito.when(paymentClient.createPayment(Mockito.any())).thenReturn(PaymentResponse.SUCCESS);


        ticketService.addTicket(createTicketDto);

        verify(paymentClient, times(1))
                .createPayment(argThat(paymentRequest ->
                        paymentRequest.totalAmount().equals(trip.getPrice())));
    }

    @Test
    void shouldCallMessageServiceWhenChecksAreCorrect() {
        trip.setPrice(new BigDecimal(111));
        Mockito.when(tripRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(trip));
        Mockito.when(paymentClient.createPayment(Mockito.any()))
                .thenReturn(PaymentResponse.SUCCESS);


        ticketService.addTicket(createTicketDto);

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("ticketing.sms"),
                        (Object) argThat(m -> ((TicketInformationMessageDto) m)
                                .userDto().phoneNumber().equals(user.getPhoneNumber())));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIdFieldsAreNotSame() {
        CreateTicketDto createTicketDto1 = new CreateTicketDto(1L, null, PassengerGender.MALE, null);
        CreateTicketDto createTicketDto2 = new CreateTicketDto(2L, null, PassengerGender.FEMALE, null);
        CreateTicketDto createTicketDto3 = new CreateTicketDto(3L, null, PassengerGender.FEMALE, null);
        List<CreateTicketDto> ticketDtoList = List.of(createTicketDto1, createTicketDto2,
                createTicketDto3);

        Throwable ex = catchThrowable(() -> ticketService.addTicketBulk(ticketDtoList));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTripIdFieldsAreNotSame() {
        CreateTicketDto createTicketDto1 = new CreateTicketDto(1L, 4L, PassengerGender.MALE, null);
        CreateTicketDto createTicketDto2 = new CreateTicketDto(1L, 5L, PassengerGender.MALE, null);
        CreateTicketDto createTicketDto3 = new CreateTicketDto(1L, 6L, PassengerGender.FEMALE, null);
        List<CreateTicketDto> ticketDtoList = List.of(createTicketDto1, createTicketDto2,
                createTicketDto3);

        Throwable ex = catchThrowable(() -> ticketService.addTicketBulk(ticketDtoList));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowUserCannotBuyMoreTicketsExceptionWhenUserTriesToBuyTicketsForMoreThanTwoMalePassengers() {
        CreateTicketDto createTicketDto1 = new CreateTicketDto(1L, 2L, PassengerGender.MALE, null);
        CreateTicketDto createTicketDto2 = new CreateTicketDto(1L, 2L, PassengerGender.MALE, null);
        CreateTicketDto createTicketDto3 = new CreateTicketDto(1L, 2L, PassengerGender.MALE, null);
        List<CreateTicketDto> ticketDtoList = List.of(createTicketDto1, createTicketDto2,
                createTicketDto3);
        user.setUserType(UserType.INDIVIDUAL);
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));

        Throwable ex = catchThrowable(() -> ticketService.addTicketBulk(ticketDtoList));

        assertThat(ex).isInstanceOf(CannotSellTicketsException.class);
    }

    @Test
    void shouldCallPaymentServiceWithTotalSumWhenAllChecksAreSuccessful() {

        Throwable ex = catchThrowable(() -> ticketService.addTicketBulk(ticketDtoList));

        verify(paymentClient, times(1)).createPayment(
                argThat(paymentRequestDto ->
                        paymentRequestDto.totalAmount()
                                .equals(trip.getPrice().multiply(BigDecimal.valueOf(3)))));
    }


    @Test
    void shouldCallPaymentServiceWithTotalSumWhenAllChecksAreSuccessfulOnAddBulk() {

        Throwable ex = catchThrowable(() -> ticketService.addTicketBulk(ticketDtoList));

        verify(paymentClient, times(1)).createPayment(
                argThat(paymentRequestDto -> paymentRequestDto.totalAmount()
                        .equals(trip.getPrice().multiply(BigDecimal.valueOf(3)))));
    }

    @Test
    void shouldCallMessageServiceWhenChecksAreCorrectOnAddBulk() {
        Mockito.when(paymentClient.createPayment(Mockito.any())).thenReturn(PaymentResponse.SUCCESS);

        ticketService.addTicketBulk(ticketDtoList);

        verify(rabbitTemplate, times(1)).convertAndSend(eq("ticketing.sms"),
                (Object) argThat(m -> ((TicketInformationMessageDto) m)
                        .userDto().phoneNumber().equals(user.getPhoneNumber())));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenWrongUserIdIsGiven() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> ticketService.getByUserId(1000L));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnAListOfDtosWhenCheckSuccessfull() {
        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        Ticket ticket3 = new Ticket();
        ticket1.setTicketId(1L);
        ticket1.setUser(user);
        ticket1.setTrip(trip);
        ticket1.setPassengerGender(PassengerGender.FEMALE);
        ticket2.setTicketId(20L);
        ticket2.setUser(user);
        ticket2.setTrip(trip);
        ticket2.setPassengerGender(PassengerGender.FEMALE);
        ticket3.setTicketId(3L);
        ticket3.setUser(user);
        ticket3.setTrip(trip);
        ticket3.setPassengerGender(PassengerGender.FEMALE);
        List<Ticket> tickets = List.of(ticket1, ticket3, ticket2);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(ticketRepository.findByUser(Mockito.any())).thenReturn(tickets);

        var ticketDtos = ticketService.getByUserId(3L);
        assertTrue(ticketDtos.stream().anyMatch(t -> t.ticketId() == 20L));
    }

    @Test
    void shouldCallRepositoryWithGivenArguments() {
        SearchTripDto searchTripDto = new SearchTripDto("depstation", "arrivalstation",
                VehicleType.BUS, LocalDate.EPOCH);
        trip.setDeparture(LocalDateTime.ofEpochSecond(0L, 0, ZoneOffset.UTC));

        List<GetTripDto> trips = ticketService.search(searchTripDto);

        verify(tripRepository, times(1)).findByDepartureStationAndArrivalStationAndVehicleType(
                "depstation", "arrivalstation", VehicleType.BUS);
    }


}




