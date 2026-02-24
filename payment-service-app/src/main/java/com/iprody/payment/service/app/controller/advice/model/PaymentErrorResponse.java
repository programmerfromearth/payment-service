package com.iprody.payment.service.app.controller.advice.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentErrorResponse(String message,
                                   Instant timestamp,
                                   String operation,
                                   UUID entityId) {

}
