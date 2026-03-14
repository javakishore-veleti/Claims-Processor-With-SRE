package com.healthcare.claims.common.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Auto-configuration for the inter-service communication client module.
 * <p>
 * Provides a {@link RestTemplate} bean pre-configured with timeout defaults and,
 * when authentication is enabled, a {@link ServiceAuthInterceptor} that adds the
 * appropriate auth headers to every outgoing request.
 */
@Configuration
@EnableConfigurationProperties(ServiceClientProperties.class)
@ComponentScan(basePackageClasses = ServiceClientAutoConfiguration.class)
public class ServiceClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate serviceRestTemplate(ServiceClientProperties props) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .build();

        if (props.getAuth().isEnabled()) {
            restTemplate.getInterceptors().add(new ServiceAuthInterceptor(props.getAuth()));
        }
        return restTemplate;
    }
}
