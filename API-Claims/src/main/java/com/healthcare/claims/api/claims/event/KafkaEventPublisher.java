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
        try {
            log.info("Publishing event to Kafka: topic={}, key={}", topic, key);
            kafkaTemplate.send(topic, key, event);
        } catch (Exception e) {
            log.error("Failed to publish to {}, sending to DLQ: {}", topic, e.getMessage());
            try {
                kafkaTemplate.send("claims-dlq", key, event);
            } catch (Exception dlqError) {
                log.error("Failed to send to DLQ: {}", dlqError.getMessage());
            }
        }
    }

    @Override
    public void publish(String topic, String key, Object event, Map<String, String> headers) {
        try {
            log.info("Publishing event to Kafka with headers: topic={}, key={}, headers={}", topic, key, headers.keySet());
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, key, event);
            headers.forEach((headerKey, headerValue) ->
                    record.headers().add(headerKey, headerValue.getBytes()));
            kafkaTemplate.send(record);
        } catch (Exception e) {
            log.error("Failed to publish to {}, sending to DLQ: {}", topic, e.getMessage());
            try {
                kafkaTemplate.send("claims-dlq", key, event);
            } catch (Exception dlqError) {
                log.error("Failed to send to DLQ: {}", dlqError.getMessage());
            }
        }
    }
}
