package com.orderservice.app.feign;

import com.orderservice.app.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CartClientFallback implements CartClient {
    @Override
    public ResponseEntity<Cart> getCart(int cartId) {
        return null;
    }

    @Override
    public Cart getCartById(int cartId) {
        return null;
    }

    @Override
    public void deleteCartById(int cartId) {

    }
}
