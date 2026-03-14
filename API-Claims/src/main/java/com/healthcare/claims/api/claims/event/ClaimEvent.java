package com.healthcare.claims.api.claims.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class ClaimEvent {
    private ClaimEventType eventType;
    private String claimId;
    private String customerId;
    private String claimNumber;
    private String stage;
    private Instant timestamp;
    private Map<String, Object> metadata;
}
