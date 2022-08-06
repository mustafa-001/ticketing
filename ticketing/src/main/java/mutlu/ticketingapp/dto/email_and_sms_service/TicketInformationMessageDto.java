package mutlu.ticketingapp.dto.email_and_sms_service;

import mutlu.ticketingapp.dto.ticket.GetTripDto;
import mutlu.ticketingapp.dto.user.GetUserDto;

public record TicketInformationMessageDto(GetUserDto userDto, GetTripDto tripDto, Long numberOfTickets) {
}
