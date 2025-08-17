package com.cartservice.app.service;

import com.cartservice.app.dto.CartDTO;
import com.cartservice.app.entity.Cart;
import com.cartservice.app.entity.Items;
import com.cartservice.app.entity.Product;
import com.cartservice.app.entity.User;
import com.cartservice.app.exception.CartServiceException;
import com.cartservice.app.feign.ProductClient;
import com.cartservice.app.feign.UserClient;
import com.cartservice.app.mapper.CartMapper;
import com.cartservice.app.repository.CartRepository;
import com.cartservice.app.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private UserClient userClient;

    @Override
    public ResponseEntity<Cart> getOrCreateCart(int customerId, String token) {
        // Extract email from JWT token
        String email = JwtUtil.extractEmail(token);

        // Call UserService to get user details
        User user = userClient.getUserByEmail(email);
        if (user == null || user.getUserId() != customerId) {
            throw new CartServiceException("Unauthorized access");
        }

        // Proceed with the existing logic
        Cart cart = Optional.ofNullable(cartRepository.findByCustomerId(customerId))
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    return cartRepository.save(newCart);
                });
        return ResponseEntity.ok(cart);
    }

    @Override
    public ResponseEntity<Cart> addItem(int itemId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null) {
            throw new CartServiceException("Could not extract email from token");
        }

        // 2. Call user-service to get user details
        User user = userClient.getUserByEmail(email);
        if (user == null) {
            throw new CartServiceException("User not found with email: " + email);
        }

        int customerId = user.getUserId();
        // Check if a cart exists for the given customer ID, if not, create one
        Cart cart = Optional.ofNullable(cartRepository.findByCustomerId(customerId))
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    return cartRepository.save(newCart);
                });


        // Fetch product details using the ProductClient
        Product product = productClient.getProductById(itemId);
        if (product == null) {
            throw new CartServiceException("Product not found with ID " + itemId);
        }
        boolean flag = true;
        for (Items i: cart.getItems()) {
            if (itemId == i.getProductId()) {
                flag = false;
                i.setQuantity(i.getQuantity()+1);
            }
        }
        // Create a new item and set its details from the product
        if (flag) {
            Items newItem = new Items();
            newItem.setCart(cart);
            newItem.setProductId(product.getProductId());
            newItem.setItemType(product.getProductType());
            newItem.setPrice(product.getPrice());
            newItem.setItemName(product.getProductName());
            newItem.setCategory(product.getCategory());
            newItem.setImage(product.getImage());
            newItem.setDescription(product.getDescription());
            newItem.setDiscount(product.getDiscount());
            newItem.setQuantity(1); // Default quantity to 1
            // Add the item to the cart
            cart.getItems().add(newItem);
        }
        // Update the total price of the cart
        cart.setTotalPrice(cartTotal(cart));

        // Save the updated cart
        cart.setCustomerId(user.getUserId());
        Cart updatedCart = cartRepository.save(cart);
        addCart(updatedCart);

        return new ResponseEntity<>(updatedCart, HttpStatus.OK);
    }


    @Override
    public void removeItem(int customerId, int itemId) {
        Cart cart = Optional.ofNullable(cartRepository.findByCustomerId(customerId))
                .orElseThrow(() -> new CartServiceException("Cart not found for customer ID " + customerId));

        boolean removed = false;
        List<Items> items = cart.getItems();
        for (Items item: items) {
            if (itemId == item.getProductId()) {
                item.setQuantity(item.getQuantity() - 1);
                removed = true;
            }
        }

        if (!removed) {
            throw new CartServiceException("Item not found with ID " + itemId);
        }

        cart.setTotalPrice(cartTotal(cart));
        cartRepository.save(cart);
    }

    @Override
    public ResponseEntity<Cart> getcartById(int cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartServiceException("Cart not found with ID " + cartId));
        return ResponseEntity.ok(cart);
    }

    @Override
    public ResponseEntity<Cart> updateCart(int cartId, @Valid Cart cart) {
        Optional<Cart> existingCart = cartRepository.findById(cartId);
        if (existingCart.isEmpty()) {
            throw new CartServiceException("Cart Id not present");
        }

        // Set the cart reference for each item
        cart.getItems().forEach(item -> {
            item.setCart(cart); // Set the bidirectional relationship
            Product product = productClient.getProductById(item.getProductId());
            if (product != null) {
                item.setPrice(product.getPrice());
            }
        });

        cart.setCartId(cartId);
        cart.setTotalPrice(cartTotal(cart));
        return new ResponseEntity<>(cartRepository.save(cart), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Cart>> getallcarts() {
        if (cartRepository.findAll().isEmpty()) {
            throw new CartServiceException("No any data present");
        }
        return new ResponseEntity<>(cartRepository.findAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Cart> addCart(@Valid Cart cart) {
        cart.getItems().forEach(item -> {
            Product product = productClient.getProductById(item.getProductId());
            if (product != null) {
                item.setPrice(product.getPrice());
            }
        });
        cart.setTotalPrice(cartTotal(cart));
        return new ResponseEntity<>(cartRepository.save(cart), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteCart(int cartId) {
        Optional<Cart> c = cartRepository.findById(cartId);
        if (c.isEmpty()) {
            throw new CartServiceException("Cart Id not present");
        }
        cartRepository.deleteById(cartId);
        return new ResponseEntity<>("Items deleted successfully", HttpStatus.OK);
    }

    @Override
    public List<CartDTO> getAllByCustomerId(int customerId) {
        Cart carts = cartRepository.findByCustomerId(customerId);
        CartDTO cartDTO = cartMapper.toDTO(carts);
        return Collections.singletonList(cartDTO);
    }

    // Helper method to calculate the total price of the cart
    public double cartTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}