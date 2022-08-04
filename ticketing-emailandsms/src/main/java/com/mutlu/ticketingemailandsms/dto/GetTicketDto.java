package com.mutlu.ticketingemailandsms.dto;


public record GetTicketDto(Long ticketId, Long userId, Long tripId, PassengerGender passengerGender) {
}
