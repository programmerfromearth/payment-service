package com.iprody.xpayment.adapter.app.service.validator.impl.strategy;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategy;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import lombok.RequiredArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Component;

import static com.iprody.xpayment.adapter.app.service.validator.model.ValidationType.DECIMAL_PLACES_BY_ISO_4217;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getFailedValidationResult;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getSuccessValidation;


@Component
@RequiredArgsConstructor
public class DecimalPlacesValidationStrategyImpl implements PaymentValidationStrategy {

    @Override
    public ValidationType getValidateType() {
        return DECIMAL_PLACES_BY_ISO_4217;
    }

    @Override
    public ValidationResult validate(XPaymentAdapterRequestMessage message) {
        try {
            final CurrencyUnit currency = CurrencyUnit.of(message.getCurrency());

            Money.of(currency, message.getAmount());

            return getSuccessValidation(getValidateType());
        } catch (Exception e) {
            return getFailedValidationResult(getValidateType());
        }
    }
}
