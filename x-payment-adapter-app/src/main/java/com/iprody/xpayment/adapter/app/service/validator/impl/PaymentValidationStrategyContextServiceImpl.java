package com.iprody.xpayment.adapter.app.service.validator.impl;

import com.iprody.common.payment.app.async.XPaymentAdapterRequestMessage;
import com.iprody.xpayment.adapter.app.exception.PaymentValidationException;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategy;
import com.iprody.xpayment.adapter.app.service.validator.api.PaymentValidationStrategyContextService;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationResult;
import com.iprody.xpayment.adapter.app.service.validator.model.ValidationType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentValidationStrategyContextServiceImpl implements PaymentValidationStrategyContextService {
    private final List<PaymentValidationStrategy> validationStrategies;

    @PostConstruct
    public void validateStrategyUniqueness() {
        final Map<ValidationType, Long> checkTypeCounts = validationStrategies.stream()
                .collect(Collectors.groupingBy(
                        PaymentValidationStrategy::getValidateType,
                        Collectors.counting()));

        final List<ValidationType> duplicateCheckTypes = checkTypeCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateCheckTypes.isEmpty()) {
            throw new IllegalStateException("Multiple validation strategies found for check types: " + duplicateCheckTypes);
        }
    }

    @Override
    public void runValidation(XPaymentAdapterRequestMessage message) {
        final List<ValidationResult> failedValidation = validationStrategies.stream()
                .sorted(Comparator.comparing(PaymentValidationStrategy::getValidateType))
                .map(it -> it.validate(message))
                .filter(it -> !it.isValid())
                .toList();

        if (!CollectionUtils.isEmpty(failedValidation)) {
            final List<ValidationType> failedValidationTypes = failedValidation.stream()
                    .map(ValidationResult::validationType)
                    .toList();

            throw new PaymentValidationException("Validations failed: %s".formatted(failedValidationTypes));
        }
    }
}
