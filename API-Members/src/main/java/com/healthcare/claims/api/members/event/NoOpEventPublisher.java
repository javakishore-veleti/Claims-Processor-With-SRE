package com.healthcare.claims.api.members.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "members.features.event-stream.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class NoOpEventPublisher implements EventPublisher {

    @Override
    public void publish(String topic, String key, Object event) {
        log.debug("Event streaming is disabled. Skipping publish to topic={}, key={}", topic, key);
    }

    @Override
    public void publish(String topic, String key, Object event, Map<String, String> headers) {
        log.debug("Event streaming is disabled. Skipping publish to topic={}, key={}", topic, key);
    }
}
