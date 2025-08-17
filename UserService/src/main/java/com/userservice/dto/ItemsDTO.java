package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemsDTO {
    private int itemId;

    private double price;

    private int quantity;

    private CartDTO cart;

    private int productId;
}
