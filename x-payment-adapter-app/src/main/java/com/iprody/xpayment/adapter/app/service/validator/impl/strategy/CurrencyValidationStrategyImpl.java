package com.iprody.xpayment.adapter.app.service.validator.impl.strategy;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategy;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.iprody.xpayment.adapter.app.service.validator.model.ValidationType.CURRENCY_NOT_NULL;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getFailedValidationResult;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getSuccessValidation;


@Component
@RequiredArgsConstructor
public class CurrencyValidationStrategyImpl implements PaymentValidationStrategy {

    @Override
    public ValidationType getValidateType() {
        return CURRENCY_NOT_NULL;
    }

    @Override
    public ValidationResult validate(XPaymentAdapterRequestMessage message) {
        if (message.getCurrency() == null) {
            return getFailedValidationResult(getValidateType());
        }

        return getSuccessValidation(getValidateType());
    }
}
