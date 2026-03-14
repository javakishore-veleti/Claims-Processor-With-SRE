package com.healthcare.claims.api.claims.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ConditionalOnBean(KafkaTemplate.class)
@ConditionalOnProperty(name = "claims.features.event-stream.provider", havingValue = "kafka", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(String topic, String key, Object event) {
        log.info("Publishing event to Kafka: topic={}, key={}", topic, key);
        kafkaTemplate.send(topic, key, event);
    }

    @Override
    public void publish(String topic, String key, Object event, Map<String, String> headers) {
        log.info("Publishing event to Kafka with headers: topic={}, key={}, headers={}", topic, key, headers.keySet());
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, event);
        headers.forEach((headerKey, headerValue) ->
                record.headers().add(headerKey, headerValue.getBytes()));
        kafkaTemplate.send(record);
    }
}
