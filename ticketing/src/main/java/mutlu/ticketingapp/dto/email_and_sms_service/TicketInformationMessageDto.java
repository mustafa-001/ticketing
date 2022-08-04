package mutlu.ticketingapp.dto.email_and_sms_service;

import mutlu.ticketingapp.dto.GetTripDto;
import mutlu.ticketingapp.dto.GetUserDto;

public record TicketInformationMessageDto(GetUserDto userDto, GetTripDto tripDto, Long numberOfTickets) {
}
