package com.localdevops.payment_service.controller;

import com.localdevops.payment_service.dto.CreatePaymentRequest;
import com.localdevops.payment_service.entity.Payment;
import com.localdevops.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestBody CreatePaymentRequest request) {

        return paymentService.createPayment(
                request.getAmount(),
                request.getCurrency(),
                request.getReference(),
                request.getCustomerId()
        );
    }
}