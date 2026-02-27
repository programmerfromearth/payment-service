package com.iprody.payment.service.app.common.impl;

import com.iprody.payment.service.app.common.api.TimeProvider;

import java.time.OffsetDateTime;

public class SystemTimeProviderImpl implements TimeProvider {
    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
