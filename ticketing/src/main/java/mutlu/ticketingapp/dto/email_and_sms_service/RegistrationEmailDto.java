package mutlu.ticketingapp.dto.email_and_sms_service;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RegistrationEmailDto(@Email String email, @NotBlank String firstName, @NotBlank String lastName) {
}
