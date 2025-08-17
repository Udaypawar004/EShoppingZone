package com.orderservice.app.controller;

import com.orderservice.app.dto.OrderResponse;
import com.orderservice.app.entity.Order;
import com.orderservice.app.entity.Status;
import com.orderservice.app.exception.OrderNotFoundException;
import com.orderservice.app.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderServiceController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping("/getAllOrders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(summary = "Get orders by customer ID")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrderByCustomerId(@PathVariable int customerId) {
        List<Order> orders = orderService.getOrderByCustomerId(customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(summary = "Place a new order")
    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody Order order) {
        OrderResponse response = orderService.placeOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Change order status")
    @PutMapping("changeOrderStatus/{orderId}")
    public ResponseEntity<Order> changeOrderStatus(@Valid @RequestBody Status orderStatus, @PathVariable String orderId) {
        Order orders = orderService.changeOrderStatus(orderStatus, orderId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Operation(summary = "Cancel an order")
    @DeleteMapping("cancelOrder/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId) {
        String orders = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }


    @Operation(summary = "Get latest orders placed within the last hour")
    @GetMapping("/latest-orders")
    public ResponseEntity<List<Order>> getLatestOrderByTime() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Order> recentOrders = orderService.getLatestOrdersWithTime(oneHourAgo);
        return ResponseEntity.ok(recentOrders);
    }

    @Operation(summary = "Get order by order ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderByOrderId(@PathVariable String orderId){
        return orderService.getOrderById(orderId).map(ResponseEntity::ok).orElseThrow(() -> new OrderNotFoundException(orderId + " not found"));
    }


    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam String sessionId) {
        return orderService.handlePaymentFallback(sessionId);
    }

    @GetMapping("/payment-failed")
    public String paymentFailed(@RequestParam String orderId) {
        return "Payment failed for Order ID: " + orderId + ". Please try again.";
    }

    @PutMapping("/payment-statusUpdate")
    public void updateOrderStatus(@RequestParam String orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
    }
}