package com.healthcare.claims.api.entitlements.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "entitlements.features")
@Data
public class FeatureToggleProperties {

    private AuthProviderConfig authProvider = new AuthProviderConfig();
    private SearchConfig search = new SearchConfig();
    private EventStreamConfig eventStream = new EventStreamConfig();
    private LogShippingConfig logShipping = new LogShippingConfig();
    private CacheConfig cache = new CacheConfig();
    private MetricsConfig metrics = new MetricsConfig();

    @Data
    public static class AuthProviderConfig {
        private String provider = "local"; // local | active-directory | okta | auth0
        private String ldapUrl;
        private String ldapBaseDn;
    }

    @Data
    public static class SearchConfig {
        private boolean enabled = false;
        private String provider = "elasticsearch"; // elasticsearch | opensearch | azure-cognitive | gcp-search
        private boolean indexOnCreate = true;
        private boolean indexOnUpdate = true;
        private boolean indexOnDelete = true;
    }

    @Data
    public static class EventStreamConfig {
        private boolean enabled = false;
        private String provider = "kafka"; // kafka | kinesis | eventhub | pubsub
        private boolean publishOnCreate = true;
        private boolean publishOnUpdate = true;
        private boolean publishOnDelete = true;
    }

    @Data
    public static class LogShippingConfig {
        private boolean enabled = false;
        private String destination = "elasticsearch"; // elasticsearch | cloudwatch | azure-monitor | gcp-logging
    }

    @Data
    public static class CacheConfig {
        private boolean enabled = false;
        private String provider = "redis"; // redis | elasticache | azure-redis | memorystore
    }

    @Data
    public static class MetricsConfig {
        private boolean enabled = true; // metrics enabled by default via actuator
        private String exporter = "prometheus"; // prometheus | cloudwatch | azure-monitor | gcp-monitoring
    }
}
