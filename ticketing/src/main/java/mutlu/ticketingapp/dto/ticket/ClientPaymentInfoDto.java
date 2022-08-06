package mutlu.ticketingapp.dto.ticket;

import mutlu.ticketingapp.enums.PaymentType;

public record ClientPaymentInfoDto(PaymentType paymentType, String cardNumber) {
}
