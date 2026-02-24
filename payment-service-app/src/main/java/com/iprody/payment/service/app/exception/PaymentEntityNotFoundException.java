package com.iprody.payment.service.app.exception;

import lombok.Getter;

import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE;

@Getter
public class PaymentEntityNotFoundException extends RuntimeException {
    private final UUID entityId;
    private final String operation;

    public PaymentEntityNotFoundException(UUID entityId, String operation) {
        super(String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, entityId));
        this.entityId = entityId;
        this.operation = operation;
    }
}
