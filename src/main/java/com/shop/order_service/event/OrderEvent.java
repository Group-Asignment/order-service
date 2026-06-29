package com.shop.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The message we publish to RabbitMQ when an order is created.
 * The Notification Service receives this and logs a notification.
 * (Kept to simple field types so it converts to JSON cleanly.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long customerId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double totalPrice;
    private String status;
}
