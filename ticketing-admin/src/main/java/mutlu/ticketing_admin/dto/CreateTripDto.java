package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.common.VehicleType;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateTripDto(VehicleType vehicleType, @NotBlank  String departureStation, @NotBlank  String arrivalStation, @FutureOrPresent LocalDateTime departureTime) {
}
