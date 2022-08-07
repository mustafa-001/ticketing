package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.enums.VehicleType;
import mutlu.ticketingapp.entity.Trip;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTripDto(@NotNull Long tripId,
                         @NotNull VehicleType vehicleType,
                         @NotBlank String departureStation,
                         @NotBlank String arrivalStation,
                         @FutureOrPresent LocalDateTime departureTime,
                         @NotNull BigDecimal price) {

    public static GetTripDto fromTrip(Trip trip) {
        return new GetTripDto(trip.getTripId(), trip.getVehicleType(), trip.getDepartureStation(),
                trip.getArrivalStation(), trip.getDeparture(), trip.getPrice());
    }
}
