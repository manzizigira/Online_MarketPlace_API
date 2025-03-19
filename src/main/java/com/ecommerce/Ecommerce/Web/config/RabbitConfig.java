package com.ecommerce.Ecommerce.Web.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue("orderQueue", true); // Durable queue
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("orderExchange");
    }

    @Bean
    public Binding binding(Queue orderQueue, TopicExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with("order.routing.key");
    }
}
