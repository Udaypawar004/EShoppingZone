package com.orderservice.app.feign;

import com.orderservice.app.dto.PaymentRequest;
import com.orderservice.app.dto.StripeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PAYMENTSERVICE" ,url = "localhost:8006/") // Change port as per your payment service
public interface PaymentClient {
    @PostMapping("/payment/create-session")
    StripeResponse createSession(@RequestBody PaymentRequest paymentRequest);

    @PostMapping("/payment/update-status")
    void updateStatus(@RequestParam String orderId, @RequestParam String status);
}