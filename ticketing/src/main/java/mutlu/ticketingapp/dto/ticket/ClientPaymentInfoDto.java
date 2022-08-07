package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.enums.PaymentType;

import javax.validation.constraints.NotBlank;

public record ClientPaymentInfoDto(PaymentType paymentType, @NotBlank String cardNumber) {
}
