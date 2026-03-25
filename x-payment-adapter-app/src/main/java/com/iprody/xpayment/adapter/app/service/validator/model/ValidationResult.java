package com.iprody.xpayment.adapter.app.service.validator.model;

import lombok.Builder;

@Builder
public record ValidationResult(ValidationType validationType, boolean isValid) {
}
