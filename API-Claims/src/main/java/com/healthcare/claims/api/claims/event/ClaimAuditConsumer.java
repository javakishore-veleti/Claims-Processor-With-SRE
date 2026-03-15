package com.healthcare.claims.api.claims.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
@Slf4j
public class ClaimAuditConsumer {

    @KafkaListener(topics = {"claims-submitted", "claims-updated", "claims-stage-changed", "claims-deleted"},
                   groupId = "claims-audit-trail",
                   autoStartup = "${claims.features.event-stream.enabled:false}")
    public void onClaimEvent(String message) {
        try {
            log.info("[AUDIT] Claim event recorded: {}", message);
            // In production: persist to audit table or send to audit service
        } catch (Exception e) {
            log.error("Error recording audit event: {}", e.getMessage(), e);
        }
    }
}
