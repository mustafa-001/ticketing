package mutlu.ticketingapp.dto.user;

import javax.validation.constraints.NotBlank;

public record UpdateUserDto(@NotBlank  String firstName, @NotBlank String lastName) {
}
