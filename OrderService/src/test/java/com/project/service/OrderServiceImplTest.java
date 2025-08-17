package com.project.service;

import com.orderservice.app.dto.*;
import com.orderservice.app.entity.*;
import com.orderservice.app.exception.OrderNotFoundException;
import com.orderservice.app.feign.*;
import com.orderservice.app.repository.OrderRepository;
import com.orderservice.app.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private CartClient cartClient;
    @Mock
    private UserClient userClient;
    @Mock
    private NotificationClient notificationClient;
    @Mock
    private NotificationDetailsDTO notificationDetailsDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock SecurityContext for email
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testPlaceOrder_Success() {
        Order order = new Order();
        order.setCartId(1);

        User user = new User();
        user.setUserId(100);

        Cart cart = new Cart();
        cart.setCartId(1);
        cart.setTotalPrice(500.0);

        AddressDto addressDto = new AddressDto();
        addressDto.setStreet("Street");
        addressDto.setCity("City");
        addressDto.setState("State");
        addressDto.setCountry("Country");
        addressDto.setZipCode("12345");

        StripeResponse stripeResponse = StripeResponse.builder()
                .sessionId("sess_123")
                .sessionUrl("http://stripe.com/session")
                .build();

        when(userClient.getUserByEmail(anyString())).thenReturn(user);
        when(cartClient.getCartById(anyInt())).thenReturn(cart);
        when(userClient.getUserAddress(anyInt())).thenReturn(addressDto);
        when(paymentClient.createSession(any(PaymentRequest.class))).thenReturn(stripeResponse);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.placeOrder(order);

        assertNotNull(response);
        assertNotNull(response.getOrderId());
        assertEquals("http://stripe.com/session", response.getPaymentUrl());
        assertEquals("sess_123", response.getSessionId());
    }

    @Test
    void testGetAllOrders_ThrowsIfEmpty() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(OrderNotFoundException.class, () -> orderService.getAllOrders());
    }

    @Test
    void testGetOrderByCustomerId_ThrowsIfEmpty() {
        when(orderRepository.findByCustomerId(anyInt())).thenReturn(Collections.emptyList());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByCustomerId(1));
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        Order order = new Order();
        order.setOrderId("order-1");
        order.setPaymentStatus(Status.PENDING);

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        orderService.updateOrderStatus("order-1", "SUCCESS");

        // Assert
        assertEquals(Status.SUCCESS, order.getPaymentStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        when(orderRepository.findById("order-404")).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderService.updateOrderStatus("order-404", "SUCCESS"));
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        order.setOrderId("order-1");
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));
        Optional<Order> result = orderService.getOrderById("order-1");
        assertTrue(result.isPresent());
        assertEquals("order-1", result.get().getOrderId());
    }
}