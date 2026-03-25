package com.iprody.xpayment.adapter.app.async;

import com.iprody.common.payment.app.async.AsyncSender;
import com.iprody.common.payment.app.async.MessageHandler;
import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterStatus;
import com.iprody.common.payment.app.time.api.TimeProvider;
import com.iprody.xpayment.adapter.app.api.XPaymentProviderGateway;
import com.iprody.xpayment.adapter.app.exception.NonRetrayableException;
import com.iprody.xpayment.adapter.app.exception.PaymentValidationException;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategyContextService;
import com.iprody.xpayment.api.model.ChargeResponse;
import com.iprody.xpayment.api.model.CreateChargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final TimeProvider timeProvider;
    private final PaymentValidationStrategyContextService paymentValidationStrategyContextService;
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final XPaymentProviderGateway xPaymentProviderGateway;

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency());
        validate(message);

        CreateChargeRequest createChargeRequest = new CreateChargeRequest();
        createChargeRequest.setAmount(message.getAmount());
        createChargeRequest.setCurrency(message.getCurrency());
        createChargeRequest.setOrder(message.getPaymentGuid());

        try {
            ChargeResponse chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequest);

            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                    chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();

            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(chargeResponse.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));
            responseMessage.setOccurredAt(timeProvider.now());

            sender.send(responseMessage);
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}",
                    message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(timeProvider.now());
            sender.send(responseMessage);
        }
    }

    private void validate(XPaymentAdapterRequestMessage message) {
        try {
            paymentValidationStrategyContextService.runValidation(message);
        } catch (PaymentValidationException ex) {
            log.error(ex.getMessage(), ex);
            throw new NonRetrayableException("Payment validation failed: %s".formatted(ex.getMessage()), ex);
        }
    }
}
