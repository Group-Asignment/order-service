package com.shop.order_service.messaging;

import com.shop.order_service.config.RabbitMQConfig;
import com.shop.order_service.event.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes order events to RabbitMQ.
 * RabbitTemplate is Spring's helper for sending messages; convertAndSend turns
 * our OrderEvent into JSON and routes it through the exchange to the queue.
 */
@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OrderEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
