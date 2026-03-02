package com.localdevops.payment_service.service;

import com.localdevops.payment_service.entity.*;
import com.localdevops.payment_service.repository.PaymentRepository;
import com.localdevops.payment_service.repository.PaymentStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;

    public Payment createPayment(BigDecimal amount, String currency, String reference, String customerId) {

        Payment payment = Payment.builder()
                .amount(amount)
                .currency(currency)
                .reference(reference)
                .customerId(customerId)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Move to PROCESSING
        transitionStatus(payment, PaymentStatus.PROCESSING);

        // Simulate processing result
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            transitionStatus(payment, PaymentStatus.SUCCESS);
        } else {
            transitionStatus(payment, PaymentStatus.FAILED);
        }

        return payment;
    }

    private void transitionStatus(Payment payment, PaymentStatus newStatus) {

        PaymentStatus oldStatus = payment.getStatus();

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        PaymentStatusHistory history = PaymentStatusHistory.builder()
                .payment(payment)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedAt(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }
}