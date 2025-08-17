package com.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String orderId;
    private String stripeSessionId;
    private String currency;
    private double amount;
    private String status;
    private String receiptUrl;
    private LocalDateTime createdAt;
}