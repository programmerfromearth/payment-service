package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.async.AsyncSender;
import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public AsyncSender<XPaymentAdapterRequestMessage> asyncSender() {
        // do nothing
        return Mockito.mock(AsyncSender.class);
    }
}
