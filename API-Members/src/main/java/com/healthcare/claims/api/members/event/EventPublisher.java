package com.healthcare.claims.api.members.event;

import java.util.Map;

public interface EventPublisher {

    void publish(String topic, String key, Object event);

    void publish(String topic, String key, Object event, Map<String, String> headers);
}
