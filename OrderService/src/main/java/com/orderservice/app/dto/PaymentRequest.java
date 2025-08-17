package com.orderservice.app.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private double amount;
    private String currency;
    private String orderId;
    private String customerEmail;
}