package com.bear.ms.PaymentService.service;

import com.bear.ms.PaymentService.entity.TransactionDetails;
import com.bear.ms.PaymentService.model.PaymentMode;
import com.bear.ms.PaymentService.model.PaymentRequest;
import com.bear.ms.PaymentService.model.PaymentResponse;
import com.bear.ms.PaymentService.repository.TransactionDetailRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Override
    public long doPayment(PaymentRequest request) {

        log.info("Recording Payment Details: {}", request);

        TransactionDetails transactionDetails = TransactionDetails
                .builder()
                .paymentDate(Instant.now())
                .paymentMode(request.getPaymentMode().name())
                .amount(request.getAmount())
                .paymentStatus("SUCCESS")
                .referenceNumber(request.getReferenceNumber())
                .orderId(request.getOrderId())
                .build();

        transactionDetailRepository.save(transactionDetails);

        log.info("Transaction Completed with Id: {}", transactionDetails.getId());
        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {

        log.info("Getting payment details for the Order Id: {}", orderId);

        TransactionDetails transactionDetails = transactionDetailRepository.findByOrderId(Long.valueOf(orderId));

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .paymentId(transactionDetails.getId())
                .amount(transactionDetails.getAmount())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .paymentDate(transactionDetails.getPaymentDate())
                .orderId(transactionDetails.getOrderId())
                .status(transactionDetails.getPaymentStatus())
                .build();

        return paymentResponse;
    }
}
