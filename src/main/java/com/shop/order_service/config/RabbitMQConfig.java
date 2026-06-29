package com.shop.order_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sets up the RabbitMQ "plumbing":
 *   - an EXCHANGE (the post office that receives messages)
 *   - a QUEUE (the mailbox messages land in)
 *   - a BINDING (the rule connecting exchange -> queue via a routing key)
 *
 * The Notification Service (M3) will listen to the SAME queue name to receive orders.
 */
@Configuration
public class RabbitMQConfig {

    // These names are shared with the Notification Service — keep them identical.
    public static final String EXCHANGE = "order.exchange";
    public static final String QUEUE = "order.created.queue";
    public static final String ROUTING_KEY = "order.created";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(QUEUE, true);   // true = durable (survives a RabbitMQ restart)
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(ROUTING_KEY);
    }

    // Send messages as JSON (instead of Java's binary format) so any service can read them.
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
