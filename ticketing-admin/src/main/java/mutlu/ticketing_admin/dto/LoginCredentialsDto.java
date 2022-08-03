package mutlu.ticketing_admin.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record LoginCredentialsDto(@Email String email, @NotBlank String password) {
}
