package com.productservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @NotBlank(message = "Product type cannot be blank")
    private String productType;

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Image URL cannot be blank") // If image is required and must not be empty
    @Pattern(regexp = "^(http|https)://.*$", message = "Image must be a valid URL") // Optional: Enforce URL format
    private String image;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    @Size(max = 1000, message = "Description must not exceed 1000 characters") // Optional: Length control
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must not be negative")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount cannot exceed 100%")
    private Double discount;

}
