package com.hdshop.configs;

import org.springframework.data.auditing.DateTimeProvider;

import java.time.OffsetDateTime;
import java.util.Optional;

public class CustomDateTimeProvider implements DateTimeProvider {

    @Override
    public Optional<java.time.temporal.TemporalAccessor> getNow() {
        return Optional.of(OffsetDateTime.now());
    }
}
