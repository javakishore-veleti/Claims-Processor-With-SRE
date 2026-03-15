package com.healthcare.claims.api.claims.event;

import com.healthcare.claims.api.claims.search.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
@RequiredArgsConstructor
@Slf4j
public class ClaimEventConsumer {

    private final SearchIndexService searchIndexService;

    @KafkaListener(topics = {"claims-submitted", "claims-updated", "claims-stage-changed"},
                   groupId = "claims-search-indexer",
                   autoStartup = "${claims.features.event-stream.enabled:false}")
    public void onClaimEvent(String message) {
        try {
            log.info("Received claim event for search indexing: {}", message);
            // In a real implementation, deserialize the message and index it
            // For now, log that the event was consumed
        } catch (Exception e) {
            log.error("Error processing claim event: {}", e.getMessage(), e);
        }
    }
}
