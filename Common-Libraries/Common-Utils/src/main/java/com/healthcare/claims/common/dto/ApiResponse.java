package com.healthcare.claims.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Generic API response wrapper.
 *
 * @param <T> the type of the response payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private ApiStatus status;
    private String message;
    private T data;
    private String tenantId;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Validation error details keyed by field name.
     */
    private Map<String, String> errors;

    // ---- Static factory methods ----

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .message("Operation completed successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.CREATED)
                .message("Resource created successfully")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.ERROR)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.NOT_FOUND)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> validationError(Map<String, String> errors) {
        return ApiResponse.<T>builder()
                .status(ApiStatus.VALIDATION_ERROR)
                .message("Validation failed")
                .errors(errors)
                .build();
    }
}
