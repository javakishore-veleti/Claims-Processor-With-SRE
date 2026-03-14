-- Claims table
CREATE TABLE IF NOT EXISTS claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    customer_id VARCHAR(100) NOT NULL,
    claim_number VARCHAR(50) NOT NULL,
    stage VARCHAR(30) NOT NULL DEFAULT 'INTAKE_RECEIVED',
    submitted_date TIMESTAMP,
    extracted_data TEXT,
    adjudication_result VARCHAR(500),
    confidence_score DECIMAL(5,4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Claim documents junction table
CREATE TABLE IF NOT EXISTS claim_documents (
    claim_id UUID NOT NULL REFERENCES claims(id) ON DELETE CASCADE,
    document_url VARCHAR(1000) NOT NULL
);

-- Indexes
CREATE INDEX idx_claims_tenant_id ON claims(tenant_id);
CREATE INDEX idx_claims_customer_id ON claims(customer_id);
CREATE INDEX idx_claims_claim_number ON claims(claim_number);
CREATE INDEX idx_claims_stage ON claims(stage);
CREATE INDEX idx_claims_tenant_stage ON claims(tenant_id, stage);
CREATE INDEX idx_claims_tenant_customer ON claims(tenant_id, customer_id);
CREATE INDEX idx_claims_created_at ON claims(created_at);
CREATE INDEX idx_claims_submitted_date ON claims(submitted_date);
CREATE UNIQUE INDEX uk_claims_claim_number ON claims(claim_number);
CREATE INDEX idx_claim_documents_claim_id ON claim_documents(claim_id);
