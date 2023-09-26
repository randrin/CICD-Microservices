package com.bear.ms.ProductService.service;

import com.bear.ms.ProductService.model.ProductRequest;
import com.bear.ms.ProductService.model.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    long addProduct(ProductRequest request);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
