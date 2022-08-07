package mutlu.ticketing_admin.service;

import mutlu.ticketing_admin.enums.VehicleType;
import mutlu.ticketing_admin.dto.CreateTripDto;
import mutlu.ticketing_admin.dto.GetTripDto;
import mutlu.ticketing_admin.entity.Ticket;
import mutlu.ticketing_admin.entity.Trip;
import mutlu.ticketing_admin.repository.TripRepository;
import mutlu.ticketing_admin.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TripServiceTest {
    @InjectMocks
    private TripService tripService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    Trip trip = new Trip();

    @BeforeEach
    void init() {
        trip.setVehicleType(VehicleType.PLANE);
        trip.setDepartureStation("depS");
        trip.setArrivalStation("arrivalS");
        trip.setDeparture(LocalDateTime.now());
        trip.setPrice(BigDecimal.TEN);
        trip.setTripId(10L);
    }

    @Test
    void shouldCallRepositoryWhenValidArgumentsGiven() {
        Mockito.when(tripRepository.save(Mockito.any())).thenReturn(trip);

        tripService.addTrip(
                new CreateTripDto(VehicleType.PLANE,
                        "departure",
                        "arrival",
                        LocalDateTime.now(),
                        BigDecimal.TEN));

        verify(tripRepository, times(1)).save(
                argThat(t -> t.getArrivalStation().equals("arrival")));
    }

    @Test
    void shouldReturnEmptyOptionalWhenWrongTripIdIsGiven() {
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Optional<GetTripDto> getTripDto = tripService.getById(1337L);
        assertThat(getTripDto).isEmpty();
    }

    @Test
    void shouldReturnTotalSumWhenCalled() {
        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();

        when(trip.getTicketList()).thenReturn(List.of(ticket2, ticket1));
        when(trip.getPrice()).thenReturn(BigDecimal.TEN);
        when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));

        BigDecimal totalGain = tripService.totalRevenueFromTrip(10L);

        assertThat(totalGain).isEqualTo(BigDecimal.TEN.multiply(BigDecimal.valueOf(2L)));
    }

    @Test
    void shouldReturnTotalOfSoldTicketsWhenCalled() {
        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        when(trip.getTicketList()).thenReturn(List.of(ticket2, ticket1));
        when(trip.getPrice()).thenReturn(BigDecimal.TEN);

        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));

        long soldTickets = tripService.totalSoldTicketsFromTrip(10L);

        assertThat(soldTickets).isEqualTo(2);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTripIdDoesNotExistsTotalGain() {
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> tripService.totalRevenueFromTrip(10L));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTripIdDoesNotExistsTotalSoldTickets() {
        Mockito.when(tripRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> tripService.totalSoldTicketsFromTrip(10L));

        assertThat(ex).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCallRepositoryWhenCalledForDelete() {

        when(tripRepository.findById(Mockito.any())).thenReturn(Optional.of(trip));

        tripService.cancel(10L);

        verify(trip, times(1)).setCancelled(true);
        verify(tripRepository, times(0)).deleteById(Mockito.any());
    }
}
