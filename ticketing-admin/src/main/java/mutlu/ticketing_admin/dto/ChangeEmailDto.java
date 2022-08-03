package mutlu.ticketing_admin.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record ChangeEmailDto(@Email String oldEmail, @Email  String newEmailFirst, @Email  String newEmailSecond, @NotBlank String password) {
}
