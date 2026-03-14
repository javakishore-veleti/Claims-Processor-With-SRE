package com.healthcare.claims.api.claims.event;

public enum ClaimEventType {
    CLAIM_CREATED,
    CLAIM_UPDATED,
    CLAIM_DELETED,
    CLAIM_STAGE_CHANGED,
    CLAIM_DOCUMENT_UPLOADED,
    CLAIM_AI_EXTRACTION_COMPLETED
}
