package com.iprody.xpayment.adapter.app.checkstate.impl;

import com.iprody.common.payment.app.async.AsyncSender;
import com.iprody.common.payment.app.async.XPaymentAdapterResponseMessage;
import com.iprody.common.payment.app.async.XPaymentAdapterStatus;
import com.iprody.xpayment.adapter.app.async.model.ChargeResponseDto;
import com.iprody.xpayment.adapter.app.checkstate.api.PaymentStatusCheckHandler;
import com.iprody.xpayment.adapter.app.mapper.XPaymentProviderMapper;
import com.iprody.xpayment.adapter.app.xpayment.provider.geatway.api.XPaymentProviderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

import static com.iprody.common.payment.app.async.XPaymentAdapterStatus.CANCELED;
import static com.iprody.common.payment.app.async.XPaymentAdapterStatus.SUCCEEDED;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {
    private static final Set<XPaymentAdapterStatus> FINAL_STATUSES = Set.of(SUCCEEDED, CANCELED);

    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final XPaymentProviderMapper xPaymentProviderMapper;

    @Override
    public boolean handle(UUID paymentGuid) {
        final ChargeResponseDto chargeResponseDto = xPaymentProviderGateway.retrieveCharge(paymentGuid);

        if (chargeResponseDto == null) {
            return false;
        }

        try {
            final XPaymentAdapterStatus status = XPaymentAdapterStatus.valueOf(chargeResponseDto.status());

            if (FINAL_STATUSES.contains(status)) {
                final XPaymentAdapterResponseMessage responseMessage =
                        xPaymentProviderMapper.toXPaymentAdapterResponseMessage(chargeResponseDto);
                sender.send(responseMessage);

                return true;
            }
        } catch (IllegalArgumentException e) {
            log.warn("Unknown status: {}", chargeResponseDto.status());
            return false;
        }

        return false;
    }
}
