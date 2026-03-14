package com.healthcare.claims.common.apps.config;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * Maps HTTP error responses from Feign calls to appropriate exceptions.
 */
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new FeignClientException(response.status(), "Bad request: " + methodKey);
            case 401 -> new FeignClientException(response.status(), "Unauthorized: " + methodKey);
            case 403 -> new FeignClientException(response.status(), "Forbidden: " + methodKey);
            case 404 -> new FeignClientException(response.status(), "Resource not found: " + methodKey);
            case 409 -> new FeignClientException(response.status(), "Conflict: " + methodKey);
            case 422 -> new FeignClientException(response.status(), "Unprocessable entity: " + methodKey);
            case 429 -> new FeignClientException(response.status(), "Too many requests: " + methodKey);
            case 500, 502, 503, 504 -> new FeignClientException(response.status(), "Server error: " + methodKey);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    /**
     * Custom exception for Feign client errors with HTTP status code.
     */
    public static class FeignClientException extends RuntimeException {

        private final int statusCode;

        public FeignClientException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
