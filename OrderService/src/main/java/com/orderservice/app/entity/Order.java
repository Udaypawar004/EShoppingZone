package com.orderservice.app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_service")
@Builder
public class Order {
    @Id
    private String orderId;

    private double amountPaid;

    private int customerId;

    private String customerName;

    private String customerEmail;

    @NotBlank(message = "Mode of payment is required")
    private String modeOfPayment;

    @PastOrPresent(message = "Order date must be in the past or present")
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Status orderStatus;

    @Enumerated(EnumType.STRING)
    private Status paymentStatus;
    private String paymentSessionId; // Stripe session ID

    private int cartId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Address> address;
}
