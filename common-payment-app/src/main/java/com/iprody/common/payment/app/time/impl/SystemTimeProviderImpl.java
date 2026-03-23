package com.iprody.common.payment.app.time.impl;


import com.iprody.common.payment.app.time.api.TimeProvider;

import java.time.OffsetDateTime;

public class SystemTimeProviderImpl implements TimeProvider {
    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
