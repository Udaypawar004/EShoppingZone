package com.orderservice.app.service;

import com.itextpdf.text.DocumentException;
import com.orderservice.app.dto.*;
import com.orderservice.app.entity.*;
import com.orderservice.app.exception.CartNotFoundException;
import com.orderservice.app.exception.OrderNotFoundException;
import com.orderservice.app.feign.CartClient;
import com.orderservice.app.feign.NotificationClient;
import com.orderservice.app.feign.PaymentClient;
import com.orderservice.app.feign.UserClient;
import com.orderservice.app.repository.OrderRepository;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.orderservice.app.entity.Status.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private CartClient cartClient;

    @Autowired
    private Invoice invoice;

    @Autowired
    private UserClient userClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private NotificationDetailsDTO notificationDetailsDTO;

    @Autowired
    private NotificationDetailsWithAttachDTO notificationDetailsWithAttachDTO;


    @Override
    public OrderResponse placeOrder(Order order) {
        String id= UUID.randomUUID().toString();
        order.setOrderId(id);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userClient.getUserByEmail(email);
        if (user == null) throw new RuntimeException("User not found for email: " + email);

        Cart cart = cartClient.getCartById(order.getCartId());
        if (cart == null) throw new CartNotFoundException("Cart not found for cartId: " + order.getCartId());

        AddressDto addressDto = userClient.getUserAddress(user.getUserId());
        if (addressDto == null) throw new RuntimeException("Address not found for userId: " + user.getUserId());

        double orderTotal = cart.getTotalPrice();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(orderTotal);
        paymentRequest.setCurrency("INR");
        paymentRequest.setOrderId(order.getOrderId());
        paymentRequest.setCustomerEmail(email);

        StripeResponse paymentResponse = paymentClient.createSession(paymentRequest);
        if(paymentResponse==null){
            return OrderResponse.builder()
                    .paymentUrl("N/A")
                    .build();

        }

        paymentResponse.setMessage("Processing");
        order.setCustomerName(user.getFullName());
        order.setCustomerEmail(user.getEmailId());
        order.setAmountPaid(orderTotal);
        order.setOrderStatus(Status.PLACED);
        order.setCustomerId(user.getUserId());
        order.setCartId(cart.getCartId());
        order.setOrderDate(java.time.LocalDate.now());
        order.setPaymentStatus(PENDING);
        order.setPaymentSessionId(paymentResponse.getSessionId());
        // order.setPaymentUrl(paymentResponse.getSessionUrl());

        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setZipCode(addressDto.getZipCode());
        address.setOrder(order);
        order.setAddress(List.of(address));

        orderRepository.save(order);

        return OrderResponse.builder()
                .paymentUrl(paymentResponse.getSessionUrl())
                .orderId(order.getOrderId())
                .sessionID(order.getPaymentSessionId())
                .status(PENDING)
                .message("Order placed, awaiting payment")
                .build();
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new OrderNotFoundException("No Orders found!!");
        }
        return orders;
    }

    @Override
    public List<Order> getOrderByCustomerId(int customerId) {
        List<Order> orderList = orderRepository.findByCustomerId(customerId);
        if (orderList.isEmpty()) {
            throw new OrderNotFoundException("You have not ordered yet!");
        }
        return orderList;
    }

    @Override
    public Order changeOrderStatus(Status orderStatus, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID " + orderId));
        order.setOrderStatus(orderStatus);
        NotificationDetailsDTO notificationDetailsDTO = new NotificationDetailsDTO();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationDetailsDTO.setRecipient(email);
        notificationDetailsDTO.setSubject("Order Status");
        notificationDetailsDTO.setMsgBody("Your Order Status is " + order.getOrderStatus());
        notificationClient.sendEmail(notificationDetailsDTO);

        return orderRepository.save(order);
    }

    @Override
    public String cancelOrder(String orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
            return "Successfully Deleted";
        } else {
            throw new OrderNotFoundException("Order not found with ID " + orderId);
        }
    }


    @Override
    public List<Order> getLatestOrdersWithTime(LocalDateTime time) {
        return orderRepository.findByOrderDateAfter(time);
    }


    @Override
    public void updateOrderStatus(String orderId, String status) {
       Order order =orderRepository.findById(orderId).get();
       if(order!=null) {
           order.setPaymentStatus(Status.SUCCESS);
           orderRepository.save(order);
       }else{
           order.setPaymentStatus(Status.FAILED);
           throw new RuntimeException();
       }
    }

    @Override
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public String handlePaymentFallback(String sessionId) {
        Order order = orderRepository.findByPaymentSessionId(sessionId);

        if (order == null || PAID.equals(order.getPaymentStatus())) {
            return "Invalid session or already paid";
        }

        order.setPaymentStatus(PAID);
        order.setOrderStatus(CONFIRMED);
        orderRepository.save(order);

        try {
            // Get cart items
            List<Items> items = cartClient.getCartById(order.getCartId()).getItems();

            // Send invoice email
            byte[] pdf = invoice.generateInvoicePdf(items, order);

            NotificationDetailsWithAttachDTO mail = new NotificationDetailsWithAttachDTO();
            mail.setRecipient(order.getCustomerEmail());
            mail.setSubject("Order Confirmation");
            mail.setMsgBody("Thank you for your payment. Please find the invoice attached.");
            mail.setPdfBytes(pdf);
            notificationClient.sendMailWithAttachment(mail);

        } catch (Exception e) {
            return "Payment done but invoice sending failed";
        }

        return "Payment Successful! Thank you for your order. invoice sent to your email.";
    }
}