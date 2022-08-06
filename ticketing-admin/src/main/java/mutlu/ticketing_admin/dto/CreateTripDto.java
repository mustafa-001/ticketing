package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.enums.VehicleType;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTripDto(VehicleType vehicleType, @NotBlank  String departureStation,
                            @NotBlank  String arrivalStation, @FutureOrPresent LocalDateTime departureTime,
                            @NotNull BigDecimal price) {
}
