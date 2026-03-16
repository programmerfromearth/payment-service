package com.iprody.payment.service.app.async.kafka;

import com.iprody.payment.service.app.async.AsyncSender;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaXPaymentAdapterRequestSender implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final KafkaTemplate<String, XPaymentAdapterRequestMessage> template;
    @Value("${app.kafka.topics.xpayment-adapter.request:xpayment-adapter.requests}")
    private String topic;

    @Override
    public void send(XPaymentAdapterRequestMessage msg) {
        final String key = msg.getPaymentGuid().toString();
        log.info("Sending XPayment Adapter request: guid={}, amount={}, currency = {} ->topic = {} ",
                msg.getPaymentGuid(), msg.getAmount(), msg.getCurrency(), topic);
        template.send(topic, key, msg);
    }
}
