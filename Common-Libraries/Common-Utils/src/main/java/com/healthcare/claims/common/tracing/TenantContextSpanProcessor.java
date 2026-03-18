package com.healthcare.claims.common.tracing;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Adds tenantId from the X-Tenant-Id request header as a low-cardinality
 * observation key on every span.  When running with the aws-signals profile
 * and ADOT Collector, this attribute flows through to CloudWatch Application
 * Signals, enabling per-tenant SLO tracking and metric filtering.
 */
@Component
@Slf4j
public class TenantContextSpanProcessor implements ObservationHandler<Observation.Context> {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String TENANT_KEY = "tenant.id";
    private static final String UNKNOWN_TENANT = "unknown";

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    @Override
    public void onStart(Observation.Context context) {
        String tenantId = resolveTenantId();
        context.addLowCardinalityKeyValue(KeyValue.of(TENANT_KEY, tenantId));
    }

    private String resolveTenantId() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null && attrs.getRequest() != null) {
                String tenantId = attrs.getRequest().getHeader(TENANT_HEADER);
                if (tenantId != null && !tenantId.isBlank()) {
                    return tenantId;
                }
            }
        } catch (Exception e) {
            log.trace("Could not resolve tenant from request context: {}", e.getMessage());
        }
        return UNKNOWN_TENANT;
    }
}
