package com.paymentservice.controllers;

import com.paymentservice.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @Autowired
    StripeService stripeService;

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/success")
    public String success(@RequestParam("sessionId") String sessionId) {
        return stripeService.success(sessionId);
    }
    @GetMapping("/error")
    public String error(@RequestParam("sessionId") String sessionId) {
        return "Error while accessing this session Id" + sessionId;
    }


}
