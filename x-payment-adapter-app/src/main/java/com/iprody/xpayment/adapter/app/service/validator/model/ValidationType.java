package com.iprody.xpayment.adapter.app.service.validator.model;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;

/**
order is crucial. For more detail see {@link com.iprody.xpayment.adapter.app.service.validator.impl.PaymentValidationStrategyContextServiceImpl#runValidation(XPaymentAdapterRequestMessage)}
 */
public enum ValidationType {
    AMOUNT_IS_POSITIVE,
    CURRENCY_NOT_NULL,
    DECIMAL_PLACES_BY_ISO_4217
}
