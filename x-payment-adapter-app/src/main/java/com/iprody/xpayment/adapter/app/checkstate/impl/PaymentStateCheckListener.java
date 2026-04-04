package com.iprody.xpayment.adapter.app.checkstate.impl;

import com.iprody.xpayment.adapter.app.checkstate.api.PaymentStatusCheckHandler;
import com.iprody.xpayment.adapter.app.checkstate.model.PaymentCheckStateMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentStateCheckListener {
    private final String exchangeName;
    private final String routingKey;
    private final String deadLetterExchangeName;
    private final String deadLetterRoutingKey;
    private final int maxRetries;
    private final long intervalMs;

    private final RabbitTemplate rabbitTemplate;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @Autowired
    public PaymentStateCheckListener(
            @Value("${app.rabbitmq.delayed-exchange-name}") String exchangeName,
            @Value("${app.rabbitmq.queue-name}") String routingKey,
            @Value("${app.rabbitmq.dead-letter-exchange}") String deadLetterExchangeName,
            @Value("${app.rabbitmq.dead-letter-routing-key}") String deadLetterRoutingKey,
            @Value("${app.rabbitmq.max-retries:60}") int maxRetries,
            @Value("${app.rabbitmq.interval-ms:60000}") long intervalMs,
            RabbitTemplate rabbitTemplate,
            PaymentStatusCheckHandler paymentStatusCheckHandler) {
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.deadLetterExchangeName = deadLetterExchangeName;
        this.deadLetterRoutingKey = deadLetterRoutingKey;
        this.maxRetries = maxRetries;
        this.intervalMs = intervalMs;

        this.rabbitTemplate = rabbitTemplate;
        this.paymentStatusCheckHandler = paymentStatusCheckHandler;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        final MessageProperties props = raw.getMessageProperties();
        final int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);
        final boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
            final PaymentCheckStateMessage newMessage =
                    new PaymentCheckStateMessage(
                            message.getChargeGuid(),
                            message.getPaymentGuid(),
                            message.getAmount(),
                            message.getCurrency());
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    routingKey,
                    newMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-delay", intervalMs);
                        m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                        return m;
                    }
            );
        } else {
            rabbitTemplate.convertAndSend(
                    deadLetterExchangeName,
                    deadLetterRoutingKey,
                    message,
                    m -> {
                        final MessageProperties messageProperties = m.getMessageProperties();

                        messageProperties.setHeader("x-retry-count", retryCount);
                        messageProperties.setHeader("x-final-status", "TIMEOUT");
                        messageProperties.setHeader("x-original-queue", props.getConsumerQueue());

                        return m;
                    }
            );
        }
    }
}
