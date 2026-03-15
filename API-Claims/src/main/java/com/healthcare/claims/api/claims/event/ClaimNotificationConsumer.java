package com.healthcare.claims.api.claims.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
@Slf4j
public class ClaimNotificationConsumer {

    @KafkaListener(topics = {"claims-stage-changed"},
                   groupId = "claims-notifications",
                   autoStartup = "${claims.features.event-stream.enabled:false}")
    public void onStageChange(String message) {
        try {
            log.info("[NOTIFY] Stage change notification: {}", message);
            // In production: send email/SMS via SNS, or push notification
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
        }
    }
}
