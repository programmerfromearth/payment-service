package com.iprody.payment.service.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CommonConstants {
    public static final String NOT_FOUND_ENTITY_EXCEPTION_MESSAGE_TEMPLATE = "Unable to find entity with id: %s";
}
