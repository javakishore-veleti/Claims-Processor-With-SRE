CREATE TABLE IF NOT EXISTS members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    member_id VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    email VARCHAR(255),
    phone VARCHAR(20),
    address VARCHAR(500),
    ssn_last4 VARCHAR(4),
    policy_number VARCHAR(50),
    policy_status VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_members_tenant_member_id ON members(tenant_id, member_id);
CREATE INDEX idx_members_tenant_id ON members(tenant_id);
CREATE INDEX idx_members_member_id ON members(member_id);
CREATE INDEX idx_members_last_name ON members(last_name);
CREATE INDEX idx_members_tenant_last_name ON members(tenant_id, last_name);
CREATE INDEX idx_members_email ON members(email);
CREATE INDEX idx_members_policy_number ON members(policy_number);
CREATE INDEX idx_members_created_at ON members(created_at);
