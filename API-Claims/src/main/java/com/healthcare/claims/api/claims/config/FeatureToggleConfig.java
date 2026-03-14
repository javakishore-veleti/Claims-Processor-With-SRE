package com.healthcare.claims.api.claims.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeatureToggleProperties.class)
public class FeatureToggleConfig {
}
