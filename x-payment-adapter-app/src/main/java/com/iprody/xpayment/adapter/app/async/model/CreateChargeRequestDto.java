package com.iprody.xpayment.adapter.app.async.model;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreateChargeRequestDto(BigDecimal amount,
                                     String currency,
                                     String customer,
                                     UUID order,
                                     String receiptEmail,
                                     Map<String, Object> metadata) {
}
