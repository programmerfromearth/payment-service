package com.iprody.payment.service.app.service.payment.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentFilter(String currency,
                            BigDecimal minAmount,
                            BigDecimal maxAmount,
                            OffsetDateTime createdAfter,
                            OffsetDateTime createdBefore) {

}
