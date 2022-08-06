package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.common.PaymentType;

public record ClientPaymentInfoDto(PaymentType paymentType, String cardNumber) {
}
