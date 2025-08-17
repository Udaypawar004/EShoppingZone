package com.cartservice.app.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Getter
@Setter
public class Cart {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must not be negative")
    private double totalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonManagedReference
    private List<@Valid Items> items = new ArrayList<>();

    @Min(value = 1, message = "Customer ID must be a positive number")
    private int customerId;

}