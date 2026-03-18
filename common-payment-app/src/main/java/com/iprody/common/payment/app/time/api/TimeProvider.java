package com.iprody.payment.service.app.common.api;

import java.time.OffsetDateTime;

public interface TimeProvider {
    OffsetDateTime now();
}
