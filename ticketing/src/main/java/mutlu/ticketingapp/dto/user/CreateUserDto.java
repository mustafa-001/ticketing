package mutlu.ticketingapp.dto.user;

import mutlu.ticketingapp.common.UserType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateUserDto(@NotNull UserType userType, @NotBlank String email, @NotBlank String phoneNumber, String firstName, String lastName,
                            @NotBlank String firstPassword, @NotBlank String secondPassword) {
}
