package com.cartservice.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    @NotBlank(message = "Item type cannot be blank")
    private String itemType;

    @NotBlank(message = "Item name cannot be blank")
    private String itemName;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Image URL cannot be blank")
    @Pattern(regexp = "^(http|https)://.*$", message = "Image must be a valid URL")
    private String image;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must not be negative")
    private double price;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must not be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount cannot exceed 100%")
    private double discount;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 1, message = "Product ID must be a positive number")
    private int productId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonBackReference
    private Cart cart;

    public Items(int i, double v, int i1, Object o, int i2) {
    }
}
