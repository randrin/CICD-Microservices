package com.bear.ms.OrderService.external.client;

import com.bear.ms.OrderService.exception.CustomException;
import com.bear.ms.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "PAYMENT-SERVICE/payment")
public interface PaymentService {

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request);

    default ResponseEntity<Void> fallback(Exception e) {
        throw  new CustomException("Payment Service is not available", "UNAVAILABLE", 500);
    }
}