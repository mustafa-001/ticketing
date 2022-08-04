package mutlu.ticketingapp.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record ChangePasswordDto(@Email String email, @NotBlank String oldPassword, @NotBlank  String newPasswordFirst, @NotBlank String newPasswordSecond) {
}
