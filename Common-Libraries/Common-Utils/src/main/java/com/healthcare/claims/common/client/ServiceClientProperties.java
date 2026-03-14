package com.healthcare.claims.common.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for inter-service communication.
 * <p>
 * Supports multiple authentication providers (none, basic, jwt, cognito, azure-ad, gcp-iam)
 * and per-service endpoint configuration with individual timeout settings.
 */
@Data
@ConfigurationProperties(prefix = "claims.service-client")
public class ServiceClientProperties {

    private AuthConfig auth = new AuthConfig();
    private Map<String, ServiceEndpoint> services = new HashMap<>();

    @Data
    public static class AuthConfig {
        /**
         * Whether inter-service authentication is enabled.
         */
        private boolean enabled = false;

        /**
         * Authentication provider: none | basic | jwt | cognito | azure-ad | gcp-iam
         */
        private String provider = "none";

        // Basic auth
        private String username;
        private String password;

        // JWT
        private String jwtSecret;
        private String jwtIssuer;

        // AWS Cognito
        private String cognitoUserPoolId;
        private String cognitoClientId;
        private String cognitoRegion;

        // Azure AD
        private String azureAdTenantId;
        private String azureAdClientId;
        private String azureAdClientSecret;
    }

    @Data
    public static class ServiceEndpoint {
        private String url;
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
    }
}
