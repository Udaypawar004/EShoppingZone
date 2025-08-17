package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private int cartId;

    private double totalPrice;

    private List<ItemsDTO> items;

    private int customerId;

    public CartDTO(int cartId, List<ItemsDTO> items) {
        this.cartId = cartId;
        this.items = items;
    }
}
