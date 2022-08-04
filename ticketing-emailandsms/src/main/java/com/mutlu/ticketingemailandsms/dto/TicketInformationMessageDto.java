package com.mutlu.ticketingemailandsms.dto;


public record TicketInformationMessageDto(GetUserDto userDto, GetTripDto tripDto, Long numberOfTickets) {
}
