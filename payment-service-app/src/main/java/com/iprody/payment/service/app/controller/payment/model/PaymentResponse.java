package com.iprody.payment.service.app.controller.payment.model;

import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record PaymentResponse(
        UUID guid,
        UUID inquiryRefId,
        BigDecimal amount,
        String currency,
        UUID transactionRefId,
        PaymentStatus status,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

}
