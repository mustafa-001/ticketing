package mutlu.ticketing_admin.dto;

import mutlu.ticketing_admin.common.UserType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateUserDto(@NotNull UserType userType, @NotBlank String email, String firstName, String lastName,
                            @NotBlank String firstPassword, @NotBlank String secondPassword) {
}
