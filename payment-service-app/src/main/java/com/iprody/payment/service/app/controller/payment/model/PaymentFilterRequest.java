package com.iprody.payment.service.app.controller.payment.model;

import com.iprody.payment.service.app.persistency.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentFilterRequest(
        PaymentStatus status,
        String currency,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        OffsetDateTime createdAfter,
        OffsetDateTime createdBefore) {

}
