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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {
        // Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                PaymentResponse.class
        )).thenReturn(getMockPaymentResponse());

        // Actual
        OrderResponse orderResponse = orderService.getOrderDetails(1);

        // Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getOrderId(),
                PaymentResponse.class
        );

        // Assertion
        assertNotNull(orderResponse);
        assertEquals(order.getOrderId(), orderResponse.getOrderId());

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.getOrderDetails(1));
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest request = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));

        long orderId = orderService.placeOrder(request);

        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(orderId, order.getOrderId());
    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_When_Place_Order_Payment_Fails_then_Order_Placed() {
        Order order = getMockOrder();
        OrderRequest request = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(request);

        verify(orderRepository, times(2)).save(any());
        verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
        verify(paymentService, times(1)).doPayment(any(PaymentRequest.class));

        assertEquals(orderId, order.getOrderId());
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .paymentMode(PaymentMode.CASH)
                .productId(1)
                .quantity(2)
                .totalAmount(1520)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .orderId(1)
                .paymentMode(PaymentMode.CASH)
                .status("ACCEPTED")
                .paymentDate(Instant.now())
                .amount(1200)
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("Iphone 12")
                .price(1200)
                .quantity(5)
                .build();
    }

    private Order getMockOrder() {

        return Order.builder()
                .orderId(1)
                .orderDate(Instant.now())
                .quantity(20)
                .productId(2)
                .amount(1200)
                .orderStatus("CREATED")
                .build();
    }
}