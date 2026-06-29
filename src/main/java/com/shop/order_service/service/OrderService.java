package com.shop.order_service.service;

import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.dto.OrderResponse;

/**
 * Interface describing WHAT the order logic does (not how).
 * The controller depends on this interface (SOLID - Dependency Inversion).
 */
public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
}
