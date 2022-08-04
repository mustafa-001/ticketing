package mutlu.ticketingapp.config;

import mutlu.ticketingapp.common.PaymentResponse;
import mutlu.ticketingapp.dto.ticket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "paymentClient", url = "${ticketing.payment.url}")
public interface PaymentClient {
    @PostMapping("/payment")
    PaymentResponse createPayment(@RequestBody ticket.PaymentRequestDto paymentRequestDto);
}
