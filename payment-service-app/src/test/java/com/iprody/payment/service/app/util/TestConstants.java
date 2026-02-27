package com.iprody.payment.service.app.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class TestConstants {
    public static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.of(2016, 1, 1, 0, 0, 0, 0, UTC);
}

