package com.healthcare.claims.common.tracing;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessSpanUtil {
    private final ObservationRegistry observationRegistry;

    /**
     * Execute a business operation within a traced span.
     * Usage: spanUtil.traced("claim.intake", () -> { ... return result; });
     */
    public <T> T traced(String spanName, Supplier<T> operation) {
        return Observation.createNotStarted(spanName, observationRegistry)
                .observe(operation);
    }

    /**
     * Execute a void business operation within a traced span.
     */
    public void traced(String spanName, Runnable operation) {
        Observation.createNotStarted(spanName, observationRegistry)
                .observe(operation);
    }

    /**
     * Execute with additional context (key-value pairs).
     */
    public <T> T traced(String spanName, String key, String value, Supplier<T> operation) {
        return Observation.createNotStarted(spanName, observationRegistry)
                .lowCardinalityKeyValue(key, value)
                .observe(operation);
    }
}
