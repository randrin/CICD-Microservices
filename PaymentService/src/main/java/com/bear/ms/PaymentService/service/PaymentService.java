package com.bear.ms.PaymentService.service;

import com.bear.ms.PaymentService.model.PaymentRequest;
import com.bear.ms.PaymentService.model.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    long doPayment(PaymentRequest request);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
