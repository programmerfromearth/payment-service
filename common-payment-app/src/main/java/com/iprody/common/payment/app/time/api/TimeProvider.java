package com.iprody.common.payment.app.time.api;

import java.time.OffsetDateTime;

public interface TimeProvider {
    OffsetDateTime now();
}
