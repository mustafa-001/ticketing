package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.UserType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateUserDto(@NotNull UserType userType, @NotBlank String email, String firstName, String lastName,
                            @NotBlank String firstPassword, @NotBlank String secondPassword) {
}
