package com.iprody.xpayment.adapter.app.common.impl;


import com.iprody.xpayment.adapter.app.common.api.TimeProvider;

import java.time.OffsetDateTime;

public class SystemTimeProviderImpl implements TimeProvider {
    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now();
    }
}
