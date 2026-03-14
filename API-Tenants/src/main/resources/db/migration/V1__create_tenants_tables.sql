CREATE TABLE IF NOT EXISTS tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    display_name VARCHAR(200),
    domain VARCHAR(200),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    plan VARCHAR(30) DEFAULT 'starter',
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    address VARCHAR(500),
    max_users INT DEFAULT 50,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_tenants_tenant_id ON tenants(tenant_id);
CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_domain ON tenants(domain);
CREATE INDEX idx_tenants_name ON tenants(name);
CREATE INDEX idx_tenants_plan ON tenants(plan);
CREATE INDEX idx_tenants_created_at ON tenants(created_at);
