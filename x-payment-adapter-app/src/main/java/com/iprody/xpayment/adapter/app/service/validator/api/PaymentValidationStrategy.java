package com.iprody.xpayment.adapter.app.service.validator.api;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;

public interface PaymentValidationStrategy {

    ValidationType getValidateType();

    ValidationResult validate(XPaymentAdapterRequestMessage xPaymentAdapterRequestMessage);
}
