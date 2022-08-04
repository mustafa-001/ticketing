package com.mutlu.ticketingpayment.dto;


import com.mutlu.ticketingpayment.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRequestDto(ClientPaymentInfoDto clientPaymentInfo, Long userId, Long tripId, BigDecimal totalAmount) {
    public Payment toPayment(){
        Payment payment = new Payment();
        payment.setUserId(this.userId);
        payment.setTripId(this.tripId);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setCardNumber(clientPaymentInfo().cardNumber());
        payment.setPaymentType(clientPaymentInfo.paymentType());
        return  payment;
    }
}
