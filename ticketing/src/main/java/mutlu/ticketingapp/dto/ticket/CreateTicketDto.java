package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.enums.PassengerGender;

import javax.validation.constraints.NotNull;

public record CreateTicketDto(@NotNull Long userId, @NotNull Long tripId, @NotNull PassengerGender passengerGender,
                              @NotNull ClientPaymentInfoDto clientPaymentInfoDto) {
}
