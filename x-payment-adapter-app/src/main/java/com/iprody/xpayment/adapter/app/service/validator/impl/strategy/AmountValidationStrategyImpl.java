package com.iprody.xpayment.adapter.app.service.validator.impl.strategy;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategy;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.iprody.xpayment.adapter.app.service.validator.model.ValidationType.AMOUNT_IS_POSITIVE;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getFailedValidationResult;
import static com.iprody.xpayment.adapter.app.service.validator.util.ValidationStrategyUtils.getSuccessValidation;


@Component
@RequiredArgsConstructor
public class AmountValidationStrategyImpl implements PaymentValidationStrategy {

    @Override
    public ValidationType getValidateType() {
        return AMOUNT_IS_POSITIVE;
    }

    @Override
    public ValidationResult validate(XPaymentAdapterRequestMessage message) {
        if (message.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return getFailedValidationResult(getValidateType());
        }

        return getSuccessValidation(getValidateType());
    }
}
