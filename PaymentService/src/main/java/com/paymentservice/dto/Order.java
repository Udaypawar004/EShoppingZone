package com.paymentservice.dto;

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
public class Order {

    private String orderId;

    private double amountPaid;

    private int customerId;


    private String modeOfPayment;


    private LocalDate orderDate;


    private Status orderStatus;

    private String paymentStatus;
    private String paymentSessionId; // Stripe session ID

    private int cartId;
    private List<Address> address;
}
