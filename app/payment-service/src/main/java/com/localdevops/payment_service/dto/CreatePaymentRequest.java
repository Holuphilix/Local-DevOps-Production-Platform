package com.localdevops.payment_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {

    private BigDecimal amount;
    private String currency;
    private String reference;
    private String customerId;
}