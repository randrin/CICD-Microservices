package com.bear.ms.PaymentService.controller;

import com.bear.ms.PaymentService.model.PaymentRequest;
import com.bear.ms.PaymentService.model.PaymentResponse;
import com.bear.ms.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request) {
        return new ResponseEntity<>(paymentService.doPayment(request), HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable String orderId) {
        return new ResponseEntity<PaymentResponse>(paymentService.getPaymentDetailsByOrderId(orderId), HttpStatus.OK);
    }
}
