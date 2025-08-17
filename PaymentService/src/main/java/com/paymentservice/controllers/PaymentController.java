package com.paymentservice.controllers;

import com.paymentservice.dto.PaymentRequest;
import com.paymentservice.dto.StripeResponse;
import com.paymentservice.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-session")
    public ResponseEntity<StripeResponse> createSession(@RequestBody PaymentRequest paymentRequest) {
        StripeResponse response = stripeService.createCheckoutSession(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-status/{orderId}/{status}")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable String orderId,@PathVariable  String status) {
        stripeService.updatePaymentStatus(orderId, status);
        return ResponseEntity.ok("Payment status updated");
    }
}