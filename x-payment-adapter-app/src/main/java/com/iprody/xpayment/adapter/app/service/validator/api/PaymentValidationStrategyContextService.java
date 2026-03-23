package com.iprody.xpayment.adapter.app.service.validator.api;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;

public interface PaymentValidationStrategyContextService {

    void runValidation(XPaymentAdapterRequestMessage message);
}
