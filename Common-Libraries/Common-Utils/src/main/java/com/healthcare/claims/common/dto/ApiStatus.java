package com.healthcare.claims.common.dto;

/**
 * Enum representing the status of an API response.
 */
public enum ApiStatus {
    SUCCESS,
    CREATED,
    UPDATED,
    DELETED,
    ERROR,
    NOT_FOUND,
    VALIDATION_ERROR,
    UNAUTHORIZED,
    FORBIDDEN
}
