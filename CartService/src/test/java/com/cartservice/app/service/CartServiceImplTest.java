package com.cartservice.app.service;

import com.cartservice.app.entity.Cart;
import com.cartservice.app.entity.Items;
import com.cartservice.app.entity.Product;
import com.cartservice.app.exception.CartServiceException;
import com.cartservice.app.feign.ProductClient;
import com.cartservice.app.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private ProductClient productClient;


    private Cart testCart;

    @BeforeEach
    void setup() {
        testCart = new Cart();
        testCart.setCartId(1);
        testCart.setCustomerId(123);

        Items item1 = new Items();
        item1.setItemId(1);
        item1.setProductId(101);
        item1.setPrice(50.0);
        item1.setQuantity(2);

        Items item2 = new Items();
        item2.setItemId(2);
        item2.setProductId(102);
        item2.setPrice(75.0);
        item2.setQuantity(1);

        testCart.setItems(List.of(item1, item2));
    }

    @Test
    void shouldReturnCartWhenIdExists() {
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));

        ResponseEntity<Cart> response = cartService.getcartById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(123, response.getBody().getCustomerId());
    }

    @Test
    void shouldThrowWhenCartIdNotFound() {
        when(cartRepository.findById(999)).thenReturn(Optional.empty());

        CartServiceException ex = assertThrows(CartServiceException.class,
                () -> cartService.getcartById(999));

        assertEquals("Cart not found with ID 999", ex.getMessage());
    }

    @Test
    void shouldThrowOnUpdateWhenCartMissing() {
        when(cartRepository.findById(999)).thenReturn(Optional.empty());

        CartServiceException ex = assertThrows(CartServiceException.class,
                () -> cartService.updateCart(999, testCart));

        assertEquals("Cart Id not present", ex.getMessage());
    }

    @Test
    void shouldReturnAllCarts() {
        when(cartRepository.findAll()).thenReturn(List.of(testCart));

        ResponseEntity<List<Cart>> response = cartService.getallcarts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldThrowWhenCartListEmpty() {
        when(cartRepository.findAll()).thenReturn(Collections.emptyList());

        CartServiceException ex = assertThrows(CartServiceException.class,
                () -> cartService.getallcarts());

        assertEquals("No any data present", ex.getMessage());
    }

    @Test
    void shouldAddCartAndCalculateTotalSuccessfully() {
        // Arrange
        Product product1 = new Product();
        product1.setProductId(101);
        product1.setPrice(50.0);

        Product product2 = new Product();
        product2.setProductId(102);
        product2.setPrice(30.0);

        when(productClient.getProductById(101)).thenReturn(product1);
        when(productClient.getProductById(102)).thenReturn(product2);

        Cart savedCart = new Cart();
        savedCart.setCartId(testCart.getCartId());
        savedCart.setCustomerId(testCart.getCustomerId());
        savedCart.setItems(testCart.getItems());
        savedCart.setTotalPrice(130.0); // (2×50) + (1×30)

        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        // Act
        ResponseEntity<Cart> response = cartService.addCart(testCart);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(130.0, response.getBody().getTotalPrice(), 0.01);
        assertEquals(2, response.getBody().getItems().size());
    }


    @Test
    void shouldCalculateCartTotalCorrectly() {
        double total = cartService.cartTotal(testCart);

        assertEquals(175.0, total, 0.01);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentCart() {
        when(cartRepository.findById(999)).thenReturn(Optional.empty());

        CartServiceException ex = assertThrows(CartServiceException.class,
                () -> cartService.deleteCart(999));

        assertEquals("Cart Id not present", ex.getMessage());
    }

}