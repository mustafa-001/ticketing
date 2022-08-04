package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.VehicleType;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public record SearchTripDto(@NotBlank String departureStation,
                            @NotBlank String arrivalStation,
                            VehicleType vehicleType,
                            @FutureOrPresent LocalDate date) {
}
