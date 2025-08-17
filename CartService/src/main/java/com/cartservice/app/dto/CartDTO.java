package com.cartservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CartDTO {

    private int cartId;

    private double totalPrice;

    private List<ItemsDTO> items;

    private int customerId;
}
