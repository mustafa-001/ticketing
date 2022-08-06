package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.enums.VehicleType;
import mutlu.ticketing_admin.entity.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTripDto(Long tripId, VehicleType vehicleType, String departureStation, String arrivalStation,
                         LocalDateTime departureTime, BigDecimal price)  {
    static public GetTripDto fromTrip(Trip trip){
        return new GetTripDto(trip.getTripId(), trip.getVehicleType(), trip.getDepartureStation(),
                trip.getArrivalStation(), trip.getDeparture(), trip.getPrice());
    }
}
