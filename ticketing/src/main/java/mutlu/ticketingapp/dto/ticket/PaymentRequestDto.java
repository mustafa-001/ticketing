package mutlu.ticketingapp.dto.ticket;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentRequestDto(@NotNull ClientPaymentInfoDto clientPaymentInfo,
                                @NotNull Long userId,
                                @NotNull Long tripId,
                                @NotNull BigDecimal totalAmount) {
}
