package mutlu.ticketing_admin.service;

import mutlu.ticketing_admin.entity.Trip;
import mutlu.ticketing_admin.repository.TripRepository;
import mutlu.ticketing_admin.dto.CreateTripDto;
import mutlu.ticketing_admin.dto.GetTripDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripService
{

    private final TripRepository tripRepository;
    Logger log = LoggerFactory.getLogger(UserService.class);

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
        return Optional.of(GetTripDto.fromTrip(trip.orElse(null)));
    }
}
