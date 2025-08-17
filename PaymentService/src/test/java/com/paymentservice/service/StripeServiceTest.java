package com.paymentservice.service;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.dto.StripeResponse;
import com.paymentservice.entity.Payment;
import com.paymentservice.feign.OrderFeignClient;
import com.paymentservice.repository.PaymentRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService paymentService; // This initializes the service with mocks

    @Mock
    private PaymentRepo paymentRepository;

    @Mock
    private OrderFeignClient orderFeignClient;

    @Test
    void testCreateCheckoutSession_Success() throws StripeException {
        // Arrange
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId("order-123");
        paymentRequest.setCurrency("inr");
        paymentRequest.setAmount(500.0);

        Session mockSession = mock(Session.class);
        when(mockSession.getId()).thenReturn("sess_123");
        when(mockSession.getUrl()).thenReturn("https://checkout.stripe.com/pay/sess_123");

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            // Act
            StripeResponse response = paymentService.createCheckoutSession(paymentRequest);

            // Assert
            assertEquals("SUCCESS", response.getStatus());
            assertEquals("Stripe session created", response.getMessage());
            assertEquals("sess_123", response.getSessionId());
            assertEquals("https://checkout.stripe.com/pay/sess_123", response.getSessionUrl());

            ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository, times(1)).save(paymentCaptor.capture());

            Payment savedPayment = paymentCaptor.getValue();
            assertEquals("order-123", savedPayment.getOrderId());
            assertEquals("usd", savedPayment.getCurrency());
            assertEquals(50.0, savedPayment.getAmount());
            assertEquals("CREATED", savedPayment.getStatus());
            assertNotNull(savedPayment.getCreatedAt());
        }
    }

    @Test
    void testUpdatePaymentStatus_WhenPaymentExists() {
        // Arrange
        String orderId = "order-123";
        String newStatus = "SUCCESS";

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setStatus("PENDING");

        when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);

        // Act
        paymentService.updatePaymentStatus(orderId, newStatus);

        // Assert
        assertEquals(newStatus, payment.getStatus());
        verify(paymentRepository, times(1)).save(payment);
        verify(orderFeignClient, times(1)).updateOrderStatus(orderId, newStatus);
    }

    @Test
    void testUpdatePaymentStatus_WhenPaymentDoesNotExist() {
        // Arrange
        String orderId = "order-999";
        String newStatus = "FAILED";

        when(paymentRepository.findByOrderId(orderId)).thenReturn(null);

        // Act
        paymentService.updatePaymentStatus(orderId, newStatus);

        // Assert
        verify(paymentRepository, never()).save(any());
        verify(orderFeignClient, never()).updateOrderStatus(anyString(), anyString());
    }

}