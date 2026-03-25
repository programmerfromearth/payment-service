package com.iprody.xpayment.adapter.app.async;

import com.iprody.common.payment.app.async.AsyncSender;
import com.iprody.common.payment.app.async.MessageHandler;
import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterStatus;
import com.iprody.common.payment.app.time.api.TimeProvider;
import com.iprody.xpayment.adapter.app.exception.NonRetryableException;
import com.iprody.xpayment.adapter.app.exception.PaymentValidationException;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategyContextService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final TimeProvider timeProvider;
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final PaymentValidationStrategyContextService paymentValidationStrategyContextService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        validate(message);
        scheduler.schedule(() -> {
            final XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            final BigDecimal amount = message.getAmount();

            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(amount);
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(defineStaus(amount));
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(timeProvider.now());

            sender.send(responseMessage);
        }, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    private void validate(XPaymentAdapterRequestMessage message) {
        try {
            paymentValidationStrategyContextService.runValidation(message);
        } catch (PaymentValidationException ex) {
            log.error(ex.getMessage(), ex);
            throw new NonRetryableException("Payment validation failed: %s".formatted(ex.getMessage()), ex);
        }
    }

    private XPaymentAdapterStatus defineStaus(BigDecimal amount) {
        final BigDecimal remainder = amount.remainder(BigDecimal.TWO);

        if (remainder.compareTo(BigDecimal.ZERO) == 0) {
            return XPaymentAdapterStatus.SUCCEEDED;
        }
        return XPaymentAdapterStatus.CANCELED;
    }
}
