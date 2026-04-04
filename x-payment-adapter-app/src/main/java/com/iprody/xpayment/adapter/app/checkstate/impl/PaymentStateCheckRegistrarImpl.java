package com.iprody.xpayment.adapter.app.checkstate.impl;

import com.iprody.xpayment.adapter.app.checkstate.api.PaymentStateCheckRegistrar;
import com.iprody.xpayment.adapter.app.checkstate.model.PaymentCheckStateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentStateCheckRegistrarImpl implements PaymentStateCheckRegistrar {
    @Value("${app.rabbitmq.delayed-exchange-name}")
    private String exchangeName;
    @Value("${app.rabbitmq.queue-name}")
    private String routingKey;
    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;
    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void register(UUID chargeGuid, UUID paymentGuid, BigDecimal amount, String currency) {
        final PaymentCheckStateMessage message =
                new PaymentCheckStateMessage(chargeGuid, paymentGuid, amount, currency);

        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                message,
                m -> {
                    final MessageProperties messageProperties = m.getMessageProperties();

                    messageProperties.setHeader("x-delay", intervalMs);
                    messageProperties.setHeader("x-retry-count", 1);

                    return m;
                });
    }
}
