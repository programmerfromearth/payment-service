package com.iprody.xpayment.adapter.app.checkstate.impl;

import com.iprody.xpayment.adapter.app.checkstate.api.PaymentStatusCheckHandler;
import com.iprody.xpayment.adapter.app.checkstate.model.PaymentCheckStateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStateCheckListener {
    @Value("${app.rabbitmq.delayed-exchange-name}")
    private String exchangeName;
    @Value("${app.rabbitmq.queue-name}")
    private String routingKey;
    @Value("${app.rabbitmq.dlx-exchange-name}")
    private String dlxExchangeName;
    @Value("${app.rabbitmq.dlx-routing-key}")
    private String dlxRoutingKey;
    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;
    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;
    private final RabbitTemplate rabbitTemplate;
    private final PaymentStatusCheckHandler paymentStatusCheckHandler;

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        final MessageProperties props = raw.getMessageProperties();
        final int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);
        final boolean paid = paymentStatusCheckHandler.handle(message.getChargeGuid());
        if (paid) {
            return;
        }
        if (retryCount < maxRetries) {
            // Планируем следующую проверку
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
            // Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(
                    dlxExchangeName,
                    dlxRoutingKey,
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
