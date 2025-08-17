package com.paymentservice.service;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.dto.StripeResponse;
import com.paymentservice.entity.Payment;
import com.paymentservice.feign.OrderFeignClient;
import com.paymentservice.repository.PaymentRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class StripeService {

    @Autowired
    private PaymentRepo paymentRepository;

    @Autowired
    OrderFeignClient orderFeignClient;

    public StripeResponse createCheckoutSession(PaymentRequest paymentRequest) {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8006/success?sessionId={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8005/orders/payment-failed?orderId=" + paymentRequest.getOrderId())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(paymentRequest.getCurrency())
                                                .setUnitAmount((long) (paymentRequest.getAmount() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Order #" + paymentRequest.getOrderId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            Session session = Session.create(params);

            Payment payment = new Payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setStripeSessionId(session.getId());
            payment.setCurrency(paymentRequest.getCurrency());
            payment.setAmount(paymentRequest.getAmount());
            payment.setStatus("CREATED");
            payment.setCreatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Stripe session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            return StripeResponse.builder()
                    .status("FAILED")
                    .message("Stripe error: " + e.getMessage())
                    .build();
        }
    }

    public void updatePaymentStatus(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            payment.setStatus(status);
            paymentRepository.save(payment);
            orderFeignClient.updateOrderStatus(orderId, status);
        }
    }

    public String success(String sessionId) {

        // Step 2: Notify order-service about payment success
        String orderServiceUrl = orderFeignClient.handlePaymentSuccess(sessionId);


        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(orderServiceUrl, String.class);

            return "Payment successful and order notified.";
        } catch (Exception e) {
            return "Payment successful and order notified.";
        }
    }
}