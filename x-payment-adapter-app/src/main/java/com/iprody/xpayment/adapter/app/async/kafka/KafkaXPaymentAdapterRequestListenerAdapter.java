package com.iprody.xpayment.adapter.app.async.kafka;

import com.iprody.xpayment.adapter.app.async.AsyncListener;
import com.iprody.xpayment.adapter.app.async.MessageHandler;
import com.iprody.xpayment.adapter.app.async.XPaymentAdapterRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {
    private final MessageHandler<XPaymentAdapterRequestMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.xpayment-adapter.request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, XPaymentAdapterRequestMessage> record, Acknowledgment ack) {
        final XPaymentAdapterRequestMessage message = record.value();
        try {
            log.info("Received XPayment Adapter request: paymentGuid={}, partition={}, offset={}",
                    message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid={}", message.getPaymentGuid(), e);
            throw e;
        }
    }
}
