package com.iprody.payment.service.app.async;

import com.iprody.payment.service.app.common.api.TimeProvider;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final TimeProvider timeProvider;
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        final UUID txId = UUID.randomUUID();

        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING), 0, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING), 10, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, null), 20, TimeUnit.SECONDS);
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId, @Nullable XPaymentAdapterStatus status) {
        log.info("Send request to process asynchronously: {}, transactionId: {}", request, txId);
        final XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();

        final BigDecimal amount = request.getAmount();

        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(amount);
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(status != null ? status : defineStatus(amount));
        result.setOccurredAt(timeProvider.now());

        resultListener.onMessage(result);
    }

    private XPaymentAdapterStatus defineStatus(BigDecimal amount) {
        final BigDecimal remainder = amount.remainder(BigDecimal.TWO);

        if (remainder.compareTo(BigDecimal.ZERO) == 0) {
            return XPaymentAdapterStatus.SUCCEEDED;
        }

        return XPaymentAdapterStatus.CANCELED;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
