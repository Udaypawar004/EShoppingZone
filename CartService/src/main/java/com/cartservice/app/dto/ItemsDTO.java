package com.cartservice.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemsDTO {
    private int itemId;

    private String itemType;

    private String itemName;

    private String category;

    private String image;

    private double price;

    private String description;

    private double discount;

    private int quantity;

    private int productId;
}
