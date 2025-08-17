package com.orderservice.app.service;

import com.orderservice.app.dto.OrderResponse;
import com.orderservice.app.entity.Order;
import com.orderservice.app.entity.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();

    List<Order> getOrderByCustomerId(int customerId);

    OrderResponse placeOrder(Order order);

    Order changeOrderStatus(Status orderStatus, String orderId);

    String cancelOrder(String orderId);

    List<Order> getLatestOrdersWithTime(LocalDateTime time);

    Optional<Order> getOrderById(String orderId);

   // void changePaymentStatus(String orderId , String status);

    void updateOrderStatus(String orderId, String status);

    String handlePaymentFallback(String sessionId);
}
