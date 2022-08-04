package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.VehicleType;
import mutlu.ticketingapp.entity.Trip;
import net.bytebuddy.asm.Advice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetTripDto(Long tripId, VehicleType vehicleType, String departureStation, String arrivalStation,
                         LocalDateTime departureTime, BigDecimal price) {

    public static GetTripDto fromTrip(Trip trip) {
        return new GetTripDto(trip.getTripId(), trip.getVehicleType(), trip.getDepartureStation(),
                trip.getArrivalStation(), trip.getDeparture(), trip.getPrice());
    }
}
