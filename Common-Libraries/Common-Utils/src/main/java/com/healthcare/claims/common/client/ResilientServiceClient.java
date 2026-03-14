package com.healthcare.claims.common.client;

import com.healthcare.claims.common.dto.ApiResponse;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Resilient inter-service HTTP client that wraps {@link RestTemplate} with
 * Resilience4j circuit-breaker, bulkhead, and retry annotations.
 * <p>
 * Usage example:
 * <pre>{@code
 * ApiResponse<ClaimDTO> response = resilientServiceClient.get(
 *     "api-claims", "/api/v1/claims/123", ClaimDTO.class);
 * }</pre>
 */
@Slf4j
@Component
public class ResilientServiceClient {

    private final RestTemplate restTemplate;
    private final ServiceClientProperties properties;

    public ResilientServiceClient(RestTemplate restTemplate, ServiceClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    /**
     * Performs a resilient GET request to the named service.
     *
     * @param serviceName  the logical service name as defined in {@code claims.service-client.services}
     * @param path         the request path (appended to the service base URL)
     * @param responseType the expected response payload type
     * @param <T>          response payload type
     * @return the API response wrapper
     */
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackGet")
    @Bulkhead(name = "default")
    @Retry(name = "default")
    public <T> ApiResponse<T> get(String serviceName, String path, Class<T> responseType) {
        String baseUrl = getServiceUrl(serviceName);
        log.debug("GET {}{}", baseUrl, path);
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    /**
     * Performs a resilient POST request to the named service.
     *
     * @param serviceName  the logical service name
     * @param path         the request path
     * @param body         the request body
     * @param responseType the expected response payload type
     * @param <T>          request body type
     * @param <R>          response payload type
     * @return the API response wrapper
     */
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackPost")
    @Bulkhead(name = "default")
    @Retry(name = "default")
    public <T, R> ApiResponse<R> post(String serviceName, String path, T body, Class<R> responseType) {
        String baseUrl = getServiceUrl(serviceName);
        log.debug("POST {}{}", baseUrl, path);
        HttpEntity<T> entity = new HttpEntity<>(body);
        ResponseEntity<ApiResponse<R>> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.POST, entity,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    /**
     * Performs a resilient PUT request to the named service.
     *
     * @param serviceName  the logical service name
     * @param path         the request path
     * @param body         the request body
     * @param responseType the expected response payload type
     * @param <T>          request body type
     * @param <R>          response payload type
     * @return the API response wrapper
     */
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackPut")
    @Bulkhead(name = "default")
    @Retry(name = "default")
    public <T, R> ApiResponse<R> put(String serviceName, String path, T body, Class<R> responseType) {
        String baseUrl = getServiceUrl(serviceName);
        log.debug("PUT {}{}", baseUrl, path);
        HttpEntity<T> entity = new HttpEntity<>(body);
        ResponseEntity<ApiResponse<R>> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.PUT, entity,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    /**
     * Performs a resilient DELETE request to the named service.
     *
     * @param serviceName  the logical service name
     * @param path         the request path
     * @param responseType the expected response payload type
     * @param <T>          response payload type
     * @return the API response wrapper
     */
    @CircuitBreaker(name = "default", fallbackMethod = "fallbackGet")
    @Bulkhead(name = "default")
    @Retry(name = "default")
    public <T> ApiResponse<T> delete(String serviceName, String path, Class<T> responseType) {
        String baseUrl = getServiceUrl(serviceName);
        log.debug("DELETE {}{}", baseUrl, path);
        ResponseEntity<ApiResponse<T>> response = restTemplate.exchange(
                baseUrl + path, HttpMethod.DELETE, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    // ---- Fallback methods ----

    private <T> ApiResponse<T> fallbackGet(String serviceName, String path,
                                           Class<T> responseType, Throwable t) {
        log.error("Circuit breaker fallback for GET {} {}: {}", serviceName, path, t.getMessage());
        return ApiResponse.error("Service " + serviceName + " is temporarily unavailable");
    }

    private <T, R> ApiResponse<R> fallbackPost(String serviceName, String path,
                                               T body, Class<R> responseType, Throwable t) {
        log.error("Circuit breaker fallback for POST {} {}: {}", serviceName, path, t.getMessage());
        return ApiResponse.error("Service " + serviceName + " is temporarily unavailable");
    }

    private <T, R> ApiResponse<R> fallbackPut(String serviceName, String path,
                                              T body, Class<R> responseType, Throwable t) {
        log.error("Circuit breaker fallback for PUT {} {}: {}", serviceName, path, t.getMessage());
        return ApiResponse.error("Service " + serviceName + " is temporarily unavailable");
    }

    // ---- Helpers ----

    private String getServiceUrl(String serviceName) {
        ServiceClientProperties.ServiceEndpoint endpoint = properties.getServices().get(serviceName);
        if (endpoint == null) {
            throw new IllegalArgumentException("Unknown service: " + serviceName
                    + ". Configure it under claims.service-client.services." + serviceName);
        }
        return endpoint.getUrl();
    }
}
