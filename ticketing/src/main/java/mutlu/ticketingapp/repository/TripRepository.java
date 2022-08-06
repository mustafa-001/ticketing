package mutlu.ticketingapp.repository;

import mutlu.ticketingapp.enums.VehicleType;
import mutlu.ticketingapp.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByDepartureStationAndArrivalStationAndVehicleType(String departureStation, String arrivalStation,  VehicleType vehicleType);
}
