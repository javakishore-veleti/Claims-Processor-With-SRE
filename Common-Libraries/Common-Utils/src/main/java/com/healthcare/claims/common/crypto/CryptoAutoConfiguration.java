package com.healthcare.claims.common.crypto;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for the crypto module.
 * Enables {@link EncryptionProperties} and component-scans the crypto package
 * so that the appropriate {@link EncryptionService} bean is registered.
 */
@Configuration
@EnableConfigurationProperties(EncryptionProperties.class)
@ComponentScan(basePackages = {"com.healthcare.claims.common.crypto", "com.healthcare.claims.common.client", "com.healthcare.claims.common.tracing"})
public class CryptoAutoConfiguration {
}
