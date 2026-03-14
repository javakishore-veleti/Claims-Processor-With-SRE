-- Tenant Usage Metrics
CREATE TABLE IF NOT EXISTS tenant_usage_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    metric_date DATE NOT NULL,
    api_call_count BIGINT DEFAULT 0,
    claims_processed INT DEFAULT 0,
    claims_approved INT DEFAULT 0,
    claims_denied INT DEFAULT 0,
    members_count INT DEFAULT 0,
    active_users_count INT DEFAULT 0,
    storage_used_bytes BIGINT DEFAULT 0,
    avg_response_time_ms DOUBLE PRECISION,
    error_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Deployment Records
CREATE TABLE IF NOT EXISTS deployment_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    environment VARCHAR(30) NOT NULL,
    cloud_provider VARCHAR(20),
    deployed_by VARCHAR(100),
    deployed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'DEPLOYED',
    commit_hash VARCHAR(50),
    release_notes TEXT,
    rollback_of UUID
);

-- Cloud Resources
CREATE TABLE IF NOT EXISTS cloud_resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50),
    cloud_provider VARCHAR(20) NOT NULL,
    resource_type VARCHAR(30) NOT NULL,
    resource_id VARCHAR(200) NOT NULL,
    resource_name VARCHAR(200),
    region VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN',
    last_checked_at TIMESTAMP,
    metadata TEXT,
    monthly_cost_estimate DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Incident Records
CREATE TABLE IF NOT EXISTS incident_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    severity VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    root_cause TEXT,
    postmortem_url VARCHAR(500),
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Incident affected services/tenants (element collections)
CREATE TABLE IF NOT EXISTS incident_affected_services (
    incident_record_id UUID NOT NULL,
    affected_services VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS incident_affected_tenants (
    incident_record_id UUID NOT NULL,
    affected_tenants VARCHAR(100)
);

-- Tenant SLO Compliance
CREATE TABLE IF NOT EXISTS tenant_slo_compliance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    slo_name VARCHAR(100) NOT NULL,
    target_value DOUBLE PRECISION,
    actual_value DOUBLE PRECISION,
    compliant BOOLEAN DEFAULT true,
    measurement_period VARCHAR(20),
    measured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_tenant_usage_tenant_date ON tenant_usage_metrics(tenant_id, metric_date);
CREATE INDEX idx_deployment_service ON deployment_records(service_name);
CREATE INDEX idx_deployment_env ON deployment_records(environment);
CREATE INDEX idx_deployment_deployed_at ON deployment_records(deployed_at);
CREATE INDEX idx_cloud_resources_provider ON cloud_resources(cloud_provider);
CREATE INDEX idx_cloud_resources_tenant ON cloud_resources(tenant_id);
CREATE INDEX idx_cloud_resources_type ON cloud_resources(resource_type);
CREATE INDEX idx_incidents_severity ON incident_records(severity);
CREATE INDEX idx_incidents_status ON incident_records(status);
CREATE INDEX idx_incidents_started_at ON incident_records(started_at);
CREATE INDEX idx_slo_compliance_tenant ON tenant_slo_compliance(tenant_id);
CREATE INDEX idx_slo_compliance_slo ON tenant_slo_compliance(slo_name);
