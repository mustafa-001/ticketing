package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.PassengerGender;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record CreateTicketDto(@NotNull Long userId, @NotNull Long tripId, @NotNull  PassengerGender passengerGender, @NotNull  ClientPaymentInfoDto clientPaymentInfoDto) {
}
