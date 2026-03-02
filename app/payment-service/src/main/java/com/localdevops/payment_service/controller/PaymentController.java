package com.localdevops.payment_service.controller;

import com.localdevops.payment_service.entity.Payment;
import com.localdevops.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Payment createPayment(@RequestParam BigDecimal amount,
                                 @RequestParam String currency,
                                 @RequestParam String reference,
                                 @RequestParam String customerId) {

        return paymentService.createPayment(amount, currency, reference, customerId);
    }
}