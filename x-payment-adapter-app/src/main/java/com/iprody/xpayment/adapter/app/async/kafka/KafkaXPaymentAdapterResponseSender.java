package com.iprody.xpayment.adapter.app.async.kafka;

import com.iprody.common.payment.app.async.AsyncSender;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterResponseSender implements AsyncSender<XPaymentAdapterResponseMessage> {
    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;
    @Value("${app.kafka.topics.xpayment-adapter.response}")
    private String topic;

    @Override
    public void send(XPaymentAdapterResponseMessage message) {
        final String key = message.getPaymentGuid().toString();
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency={} -> topic={}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency(), topic);
        template.send(topic, key, message);
    }
}
