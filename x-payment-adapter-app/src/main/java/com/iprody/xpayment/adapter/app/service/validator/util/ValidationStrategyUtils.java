package com.iprody.xpayment.adapter.app.service.validator.util;

import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ValidationStrategyUtils {

    public static ValidationResult getSuccessValidation(ValidationType validationType) {
        return ValidationResult.builder()
                .validationType(validationType)
                .isValid(true)
                .build();
    }

    public static ValidationResult getFailedValidationResult(ValidationType validationType) {
        return ValidationResult.builder()
                .validationType(validationType)
                .isValid(false)
                .build();
    }
}
