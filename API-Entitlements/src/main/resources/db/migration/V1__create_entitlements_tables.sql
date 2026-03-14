-- Roles
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    system_role BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Privileges
CREATE TABLE IF NOT EXISTS privileges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    resource VARCHAR(50),
    action VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Groups
CREATE TABLE IF NOT EXISTS groups_table (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Users
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    password_hash VARCHAR(255),
    phone VARCHAR(20),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Group Roles
CREATE TABLE IF NOT EXISTS group_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    group_id UUID NOT NULL,
    role_id UUID NOT NULL
);

-- Group Role Privileges
CREATE TABLE IF NOT EXISTS group_role_privileges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    group_role_id UUID NOT NULL,
    privilege_id UUID NOT NULL
);

-- User Groups
CREATE TABLE IF NOT EXISTS user_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    group_id UUID NOT NULL
);

-- User Role Privileges (user-specific, beyond group level)
CREATE TABLE IF NOT EXISTS user_role_privileges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    privilege_id UUID NOT NULL
);

-- Indexes for roles
CREATE INDEX idx_roles_tenant_id ON roles(tenant_id);
CREATE UNIQUE INDEX uk_roles_tenant_name ON roles(tenant_id, name);

-- Indexes for privileges
CREATE INDEX idx_privileges_tenant_id ON privileges(tenant_id);
CREATE UNIQUE INDEX uk_privileges_tenant_name ON privileges(tenant_id, name);
CREATE INDEX idx_privileges_resource ON privileges(resource);

-- Indexes for groups
CREATE INDEX idx_groups_tenant_id ON groups_table(tenant_id);

-- Indexes for users
CREATE UNIQUE INDEX uk_users_tenant_username ON users(tenant_id, username);
CREATE UNIQUE INDEX uk_users_tenant_email ON users(tenant_id, email);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- Indexes for junction tables
CREATE INDEX idx_group_roles_group_id ON group_roles(group_id);
CREATE INDEX idx_group_roles_role_id ON group_roles(role_id);
CREATE INDEX idx_group_role_privileges_group_role_id ON group_role_privileges(group_role_id);
CREATE INDEX idx_user_groups_user_id ON user_groups(user_id);
CREATE INDEX idx_user_groups_group_id ON user_groups(group_id);
CREATE INDEX idx_user_role_privileges_user_id ON user_role_privileges(user_id);
