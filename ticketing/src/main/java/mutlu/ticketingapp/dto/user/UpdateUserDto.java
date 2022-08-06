package mutlu.ticketingapp.dto.user;

import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

public record UpdateUserDto(@NonNull Long userId, @NotBlank  String firstName, @NotBlank String lastName) {
}
