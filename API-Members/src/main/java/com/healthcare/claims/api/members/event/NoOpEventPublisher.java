package com.healthcare.claims.api.members.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnMissingBean(KafkaEventPublisher.class)
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
