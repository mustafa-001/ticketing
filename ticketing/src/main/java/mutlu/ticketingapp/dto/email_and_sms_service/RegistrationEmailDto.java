package mutlu.ticketingapp.dto.email_and_sms_service;

import mutlu.ticketingapp.dto.user.GetUserDto;

public record RegistrationEmailDto(GetUserDto userDto) {
}
