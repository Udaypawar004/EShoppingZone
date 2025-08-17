package com.cartservice.app.entity;

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
public class Product {
	@Min(value = 1, message = "Product ID must be a positive number")
    private int productId;

    @NotBlank(message = "Product type cannot be blank")
    private String productType;

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

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

}
