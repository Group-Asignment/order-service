package com.shop.order_service.exception;

/**
 * Thrown when Order Service asks Product Service for a product id that doesn't
 * exist (Product Service replies 404). We'll turn this into a clean error
 * response in PR 5 (global exception handling).
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }
}
