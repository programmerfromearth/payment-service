package com.iprody.xpayment.adapter.app.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqPaymentRetryConfig {
    private final String queueName;
    private final String delayedExchangeName;

    public RabbitMqPaymentRetryConfig(
            @Value("${app.rabbitmq.queue-name}") String queueName,
            @Value("${app.rabbitmq.delayed-exchange-name}") String delayedExchangeName) {
        this.queueName = queueName;
        this.delayedExchangeName = delayedExchangeName;
    }

    @Bean
    public Queue xpaymentQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", "payments.dlx")
                .withArgument("x-dead-letter-routing-key", "payments.dead")
                .build();
    }

    @Bean
    public CustomExchange delayedExchange() {
        return new CustomExchange(
                delayedExchangeName, "x-delayed-message", true, false, Map.of("x-delayed-type", "direct"));

    }

    @Bean
    public Binding queueBinding(Queue xpaymentQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(xpaymentQueue).to(delayedExchange).with(queueName).noargs();
    }
}
