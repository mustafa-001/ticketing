package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.PassengerGender;

public record CreateTicketDto(Long userId, Long tripId, PassengerGender passengerGender, ClientPaymentInfoDto paymentDto) {
}
