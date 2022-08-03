package mutlu.ticketingapp.dto;

import mutlu.ticketingapp.dto.ClientPaymentInfoDto;

import java.math.BigDecimal;

public record PaymentRequestDto(ClientPaymentInfoDto clientPaymentInfo, Long userId, BigDecimal totalAmount) {
}
