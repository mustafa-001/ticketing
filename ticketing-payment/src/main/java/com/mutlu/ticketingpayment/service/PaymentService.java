package com.mutlu.ticketingpayment.service;

import com.mutlu.ticketingpayment.dto.PaymentRequestDto;
import com.mutlu.ticketingpayment.common.PaymentResponse;
import com.mutlu.ticketingpayment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse addPayment(PaymentRequestDto request) {
        log.info("Saving payment {}", request);
        if (paymentRepository.save(request.toPayment()) != null) {
            return PaymentResponse.SUCCESS;
        }
        return PaymentResponse.INCOMPLETE;
    }
}
