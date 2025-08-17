package com.paymentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="ORDERSERVICE")
public interface OrderFeignClient {

    @GetMapping("/orders/payment-success")
    public String handlePaymentSuccess(@RequestParam String sessionId);

    @PutMapping("/orders/payment-statusUpdate")
    void updateOrderStatus(@RequestParam("orderId") String orderId, @RequestParam("status") String status);
}