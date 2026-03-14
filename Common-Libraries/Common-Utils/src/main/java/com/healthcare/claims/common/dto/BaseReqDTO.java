package com.healthcare.claims.common.dto;

import lombok.Data;

/**
 * Base request DTO that carries the encrypted tenant identifier.
 * All multi-tenant request DTOs should extend this class.
 */
@Data
public abstract class BaseReqDTO {

    /**
     * Encrypted tenant ID (required for all multi-tenant operations).
     */
    private String tenantId;
}
