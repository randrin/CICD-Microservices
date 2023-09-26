package com.bear.ms.OrderService.service;

import com.bear.ms.OrderService.model.OrderRequest;
import com.bear.ms.OrderService.model.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    long placeOrder(OrderRequest request);

    OrderResponse getOrderDetails(long orderId);
}
