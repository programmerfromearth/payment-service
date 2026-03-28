package com.iprody.xpayment.adapter.app.async;

import com.iprody.common.payment.app.async.AsyncSender;
import com.iprody.common.payment.app.async.MessageHandler;
import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterStatus;
import com.iprody.xpayment.adapter.app.async.model.ChargeResponseDto;
import com.iprody.xpayment.adapter.app.async.model.CreateChargeRequestDto;
import com.iprody.xpayment.adapter.app.exception.NonRetryableException;
import com.iprody.xpayment.adapter.app.exception.PaymentValidationException;
import com.iprody.xpayment.adapter.app.mapper.XPaymentProviderMapper;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategyContextService;
import com.iprody.xpayment.adapter.app.xpayment.provider.geatway.api.XPaymentProviderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final PaymentValidationStrategyContextService paymentValidationStrategyContextService;
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final XPaymentProviderMapper xPaymentProviderMapper;

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency());

        validate(message);
        final CreateChargeRequestDto createChargeRequestDto = xPaymentProviderMapper.toCreateChargeRequestDto(message);

        try {
            final ChargeResponseDto chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequestDto);

            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                    chargeResponse.status());

            final XPaymentAdapterResponseMessage responseMessage =
                    xPaymentProviderMapper.toXPaymentAdapterResponseMessage(chargeResponse);

            sender.send(responseMessage);
        } catch (RestClientException ex) {
            log.error("Error in time of sending payment request with paymentGuid - {}",
                    message.getPaymentGuid(), ex);

            final XPaymentAdapterResponseMessage responseMessage =
                    xPaymentProviderMapper.toXPaymentAdapterResponseMessage(message);
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);

            sender.send(responseMessage);
        }
    }

    private void validate(XPaymentAdapterRequestMessage message) {
        try {
            paymentValidationStrategyContextService.runValidation(message);
        } catch (PaymentValidationException ex) {
            log.error(ex.getMessage(), ex);
            throw new NonRetryableException("Payment validation failed: %s".formatted(ex.getMessage()), ex);
        }
    }
}
