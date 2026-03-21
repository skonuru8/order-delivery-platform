package com.platform.main.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange name used across the platform
    // Phase 3 will add queues, bindings, and consumers
    public static final String ORDER_EXCHANGE = "order.events";

    @Bean
    public TopicExchange orderExchange() {
        // durable=true — survives RabbitMQ restart
        // autoDelete=false — stays even when no consumers
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }
}