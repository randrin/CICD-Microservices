package com.bear.ms.OrderService.service;

import com.bear.ms.OrderService.entity.Order;
import com.bear.ms.OrderService.exception.CustomException;
import com.bear.ms.OrderService.external.client.PaymentService;
import com.bear.ms.OrderService.external.client.ProductService;
import com.bear.ms.OrderService.external.request.PaymentRequest;
import com.bear.ms.OrderService.external.response.PaymentResponse;
import com.bear.ms.OrderService.model.OrderRequest;
import com.bear.ms.OrderService.model.OrderResponse;
import com.bear.ms.OrderService.model.PaymentMode;
import com.bear.ms.OrderService.model.ProductResponse;
import com.bear.ms.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest request) {

        log.info("Placing Order Request: {}", request);

        productService.reduceQuantity(request.getProductId(), request.getQuantity());

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(request.getTotalAmount())
                .orderStatus("CREATED")
                .productId(request.getProductId())
                .orderDate(Instant.now())
                .quantity(request.getQuantity())
                .build();

        order = orderRepository.save(order);

        log.info("Calling Payment Service to complete payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .amount(request.getTotalAmount())
                .orderId(order.getOrderId())
                .paymentMode(request.getPaymentMode())
                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully, Changing the order status");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.info("Error occurred in payment. Changing order status to FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        order = orderRepository.save(order);

        log.info("Order Places successfully with Order Id: {}", order);
        return order.getOrderId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {

        log.info("Get order details for Order ID : {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order Id: " + orderId, "NOT_FOUND", 404));

        log.info("Invoking Product service to fetch the products details", + order.getOrderId());
        ProductResponse productResponse =
                restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

        ProductResponse productDetails = ProductResponse.builder()
                .price(productResponse.getPrice())
                .productName(productResponse.getProductName())
                .quantity(productResponse.getQuantity())
                .productId(productResponse.getProductId())
                .build();

        log.info("Getting payment information from the payment Service");
        PaymentResponse paymentResponse =
                restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(), PaymentResponse.class);

        PaymentResponse paymentDetails = PaymentResponse.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .amount(paymentResponse.getAmount())
                .status(paymentResponse.getStatus())
                .orderId(paymentResponse.getOrderId())
                .build();

        final OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
