package com.bear.ms.GatewayService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallback() {
        return "Order service in down!";
    }

    @GetMapping("/paymentServiceFallback")
    public String paymentServiceFallback() {
        return "Payment service in down!";
    }

    @GetMapping("/productServiceFallback")
    public String productServiceFallback() {
        return "Product service in down!";
    }
}
