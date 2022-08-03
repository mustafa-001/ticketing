package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.common.PaymentType;

public record ClientPaymentInfoDto(PaymentType paymentType, String cardNumber) {
}
