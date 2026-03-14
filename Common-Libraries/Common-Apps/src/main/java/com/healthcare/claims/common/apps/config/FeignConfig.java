package com.healthcare.claims.common.apps.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * Intercepts all Feign requests to add the X-Tenant-Id header
     * from the current request context.
     */
    @Bean
    public RequestInterceptor tenantIdRequestInterceptor() {
        return requestTemplate -> {
            // The tenantId can be sourced from a ThreadLocal, SecurityContext, or MDC.
            // For now, this is a placeholder that services can override or extend.
            String tenantId = TenantContextHolder.getTenantId();
            if (tenantId != null && !tenantId.isBlank()) {
                requestTemplate.header("X-Tenant-Id", tenantId);
            }
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Simple ThreadLocal holder for the current tenant ID.
     * Services should set this in a filter or interceptor.
     */
    public static class TenantContextHolder {

        private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

        public static void setTenantId(String tenantId) {
            TENANT_ID.set(tenantId);
        }

        public static String getTenantId() {
            return TENANT_ID.get();
        }

        public static void clear() {
            TENANT_ID.remove();
        }
    }
}
