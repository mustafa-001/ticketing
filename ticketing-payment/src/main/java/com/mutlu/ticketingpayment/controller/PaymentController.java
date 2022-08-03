package com.mutlu.ticketingpayment.controller;


import com.mutlu.ticketingpayment.dto.PaymentRequestDto;
import com.mutlu.ticketingpayment.common.PaymentResponse;
import com.mutlu.ticketingpayment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService ticketService;

    @Autowired
    public PaymentController(PaymentService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping()
    public PaymentResponse add(@RequestBody PaymentRequestDto request){
        return ticketService.addPayment(request);
    }
}
