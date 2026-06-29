package com.shop.order_service.service;

import com.shop.order_service.client.ProductClient;
import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.dto.OrderResponse;
import com.shop.order_service.dto.ProductResponse;
import com.shop.order_service.event.OrderEvent;
import com.shop.order_service.messaging.OrderEventPublisher;
import com.shop.order_service.model.Order;
import com.shop.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * The heart of Order Service. createOrder runs the full workflow:
 *   1. Ask Product Service for the product's details (name + price).
 *   2. Calculate the total price.
 *   3. Save the order to the database.
 * (Publishing to RabbitMQ is added in the next PR.)
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final OrderEventPublisher orderEventPublisher;

    // Constructor injection of the repository, the Product Service client, and the publisher.
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductClient productClient,
                            OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Call Product Service to get the product (throws ProductNotFoundException if missing)
        ProductResponse product = productClient.getProductById(request.getProductId());

        // 2. Calculate the total price
        double totalPrice = product.getUnitPrice() * request.getQuantity();

        // 3. Build and save the order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(product.getProductId());
        order.setProductName(product.getName());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CREATED");

        Order saved = orderRepository.save(order);

        // 4. Publish an "order created" event to RabbitMQ (Notification Service will consume it)
        OrderEvent event = new OrderEvent(
                saved.getOrderId(),
                saved.getCustomerId(),
                saved.getProductId(),
                saved.getProductName(),
                saved.getQuantity(),
                saved.getTotalPrice(),
                saved.getStatus()
        );
        orderEventPublisher.publish(event);

        // 5. Return the saved order as a response DTO
        return toResponse(saved);
    }

    // Maps an Order entity to an OrderResponse DTO
    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getOrderDate(),
                order.getStatus()
        );
    }
}
