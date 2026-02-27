package com.iprody.payment.service.app.exception;

import java.util.UUID;

import static com.iprody.payment.service.app.util.CommonConstants.NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(UUID entityId) {
        super(String.format(NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE, entityId));
    }
}
