package com.healthcare.claims.common.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * {@link ClientHttpRequestInterceptor} that adds authentication headers to outgoing
 * inter-service HTTP requests based on the configured provider.
 * <p>
 * Supported providers:
 * <ul>
 *   <li><b>none</b> — no authentication header is added</li>
 *   <li><b>basic</b> — HTTP Basic Authentication</li>
 *   <li><b>jwt</b> — HMAC-SHA256 signed JWT Bearer token</li>
 *   <li><b>cognito</b> — AWS Cognito (placeholder)</li>
 *   <li><b>azure-ad</b> — Azure Active Directory (placeholder)</li>
 *   <li><b>gcp-iam</b> — Google Cloud IAM (placeholder)</li>
 * </ul>
 */
@Slf4j
public class ServiceAuthInterceptor implements ClientHttpRequestInterceptor {

    private final ServiceClientProperties.AuthConfig authConfig;

    public ServiceAuthInterceptor(ServiceClientProperties.AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        switch (authConfig.getProvider()) {
            case "basic" -> {
                String credentials = authConfig.getUsername() + ":" + authConfig.getPassword();
                String encoded = Base64.getEncoder()
                        .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
                request.getHeaders().add("Authorization", "Basic " + encoded);
            }
            case "jwt" -> {
                String token = generateJwt();
                request.getHeaders().add("Authorization", "Bearer " + token);
            }
            case "cognito", "azure-ad", "gcp-iam" -> {
                // Placeholder: In production, use the respective SDK to acquire a token.
                log.debug("Cloud auth provider '{}' configured — token acquisition not yet implemented",
                        authConfig.getProvider());
            }
            // "none" — no auth header added
            default -> log.trace("No auth header added (provider='{}')", authConfig.getProvider());
        }
        return execution.execute(request, body);
    }

    /**
     * Generates a minimal HMAC-SHA256 signed JWT for inter-service communication.
     * <p>
     * The token contains an issuer ({@code iss}), issued-at ({@code iat}), and
     * expiration ({@code exp}) set to 5 minutes from now.
     */
    private String generateJwt() {
        try {
            String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            long now = Instant.now().getEpochSecond();
            String payload = base64Url(String.format(
                    "{\"iss\":\"%s\",\"iat\":%d,\"exp\":%d}",
                    authConfig.getJwtIssuer(), now, now + 300));

            String signingInput = header + "." + payload;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    authConfig.getJwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

            return signingInput + "." + signature;
        } catch (Exception e) {
            log.error("Failed to generate JWT for inter-service auth", e);
            throw new IllegalStateException("JWT generation failed", e);
        }
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
