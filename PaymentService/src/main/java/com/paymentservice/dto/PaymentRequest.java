package com.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private String currency;
    private String orderId;
    private String customerEmail;
}