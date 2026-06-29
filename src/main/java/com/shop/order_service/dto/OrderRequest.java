package com.shop.order_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * The JSON a client SENDS to create an order.
 * Note: the client only sends customerId, productId, and quantity.
 * We work out the productName and totalPrice ourselves by calling Product Service.
 */
@Data
public class OrderRequest {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than 0")
    private Integer quantity;
}
