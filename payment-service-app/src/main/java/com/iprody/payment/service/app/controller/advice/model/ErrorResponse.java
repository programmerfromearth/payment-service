package com.iprody.payment.service.app.controller.advice.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(String message,
                            Instant timestamp) {

}
