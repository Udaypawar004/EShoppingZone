package com.cartservice.app.mapper;

import com.cartservice.app.dto.CartDTO;
import com.cartservice.app.dto.ItemsDTO;
import com.cartservice.app.entity.Cart;
import com.cartservice.app.entity.Items;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {
    public CartDTO toDTO(Cart cart) {
        if (cart == null) return null;
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setCustomerId(cart.getCustomerId());
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setItems(cart.getItems().stream().map(this::toItemsDTO).collect(Collectors.toList()));
        return dto;
    }

    public ItemsDTO toItemsDTO(Items item) {
        if (item == null) return null;
        ItemsDTO dto = new ItemsDTO();
        dto.setItemId(item.getItemId());
        dto.setItemType(item.getItemType());
        dto.setItemName(item.getItemName());
        dto.setCategory(item.getCategory());
        dto.setImage(item.getImage());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setDiscount(item.getDiscount());
        dto.setQuantity(item.getQuantity());
        dto.setProductId(item.getProductId());
        return dto;
    }

    public Cart toEntity(CartDTO dto) {
        if (dto == null) return null;
        Cart cart = new Cart();
        cart.setCartId(dto.getCartId());
        cart.setCustomerId(dto.getCustomerId());
        cart.setTotalPrice(dto.getTotalPrice());
        return cart;
    }
}