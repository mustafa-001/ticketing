package mutlu.ticketing_admin.service;

import mutlu.ticketing_admin.entity.Trip;
import mutlu.ticketing_admin.repository.TripRepository;
import mutlu.ticketing_admin.dto.CreateTripDto;
import mutlu.ticketing_admin.dto.GetTripDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Service
public class TripService {

    private final TripRepository tripRepository;
    Logger log = LoggerFactory.getLogger(AdminUserService.class);

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public GetTripDto addTrip(CreateTripDto createTripDto) {
        Trip trip = new Trip();
        trip.setVehicleType(createTripDto.vehicleType())
                .setDepartureStation(createTripDto.departureStation())
                .setArrivalStation(createTripDto.arrivalStation())
                .setDeparture(createTripDto.departureTime());
        Trip savedTrip = tripRepository.save(trip);
        return GetTripDto.fromTrip(savedTrip);
    }

    public Optional<GetTripDto> getById(Long tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        log.debug("Trip for id {} is {}", tripId, trip.orElse(null));
        //TODO
        if (trip.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(GetTripDto.fromTrip(trip.orElse(null)));
    }

    public BigDecimal totalRevenueFromTrip(Long tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        log.debug("Trip for id {} is {}", tripId, trip.orElse(null));
        if (trip.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return trip.get().getPrice().multiply(BigDecimal.valueOf(trip.get().getTicketList().size()));
    }

    public long totalSoldTicketsFromTrip(Long tripId) {
        Optional<Trip> trip = tripRepository.findById(tripId);
        log.debug("Trip for id {} is {}", tripId, trip.orElse(null));
        if (trip.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return trip.get().getTicketList().size();
    }

    public void delete(Long tripId) {
        log.debug("Deleting trip with Id: {}", tripId);
        tripRepository.deleteById(tripId);
    }
}
