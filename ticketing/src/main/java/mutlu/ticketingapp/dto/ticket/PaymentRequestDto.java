package mutlu.ticketingapp.dto.ticket;

import java.math.BigDecimal;

public record PaymentRequestDto(ClientPaymentInfoDto clientPaymentInfo, Long userId, Long tripId,
                                BigDecimal totalAmount) {
}
