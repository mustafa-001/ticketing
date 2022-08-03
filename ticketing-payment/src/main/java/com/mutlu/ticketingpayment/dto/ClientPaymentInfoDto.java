package com.mutlu.ticketingpayment.dto;


import com.mutlu.ticketingpayment.common.PaymentType;

public record ClientPaymentInfoDto(PaymentType paymentType, String cardNumber) {
}
