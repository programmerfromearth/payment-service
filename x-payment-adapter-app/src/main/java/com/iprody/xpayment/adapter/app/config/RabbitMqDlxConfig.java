package com.iprody.xpayment.adapter.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqDlxConfig {
    private final String deadLetterExchangeName;
    private final String deadLetterQueue;
    private final String deadLetterRoutingKey;

    public RabbitMqDlxConfig(@Value("${app.rabbitmq.dead-letter-exchange}") String deadLetterExchangeName,
                             @Value("${app.rabbitmq.dead-letter-queue}") String deadLetterQueue,
                             @Value("${app.rabbitmq.dead-letter-routing-key}") String deadLetterRoutingKey) {
        this.deadLetterExchangeName = deadLetterExchangeName;
        this.deadLetterQueue = deadLetterQueue;
        this.deadLetterRoutingKey = deadLetterRoutingKey;
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueue).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(deadLetterRoutingKey);
    }
}
