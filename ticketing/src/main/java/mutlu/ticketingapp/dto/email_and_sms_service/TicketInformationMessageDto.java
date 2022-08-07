package mutlu.ticketingapp.dto.email_and_sms_service;

import mutlu.ticketingapp.dto.ticket.GetTripDto;
import mutlu.ticketingapp.dto.user.GetUserDto;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;

public record TicketInformationMessageDto(GetUserDto userDto, GetTripDto tripDto, @NotBlank Long numberOfTickets) {
}
