package com.healthcare.claims.api.members.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class MemberEvent {
    private MemberEventType eventType;
    private String memberId;
    private String memberNumber;
    private String firstName;
    private String lastName;
    private Instant timestamp;
    private Map<String, Object> metadata;
}
