package com.healthcare.claims.api.entitlements.seed;

import com.healthcare.claims.api.entitlements.model.Privilege;
import com.healthcare.claims.api.entitlements.model.Role;
import com.healthcare.claims.api.entitlements.repository.PrivilegeRepository;
import com.healthcare.claims.api.entitlements.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntitlementSeeder implements CommandLineRunner {

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.findByTenantId(DEFAULT_TENANT_ID).isEmpty()) {
            log.info("Seeding default roles and privileges for tenant: {}", DEFAULT_TENANT_ID);
            seedRoles();
            seedPrivileges();
            log.info("Seeding complete");
        } else {
            log.info("Roles already exist for tenant: {}, skipping seed", DEFAULT_TENANT_ID);
        }
    }

    private void seedRoles() {
        createRole("SUPER_ADMIN", "Super administrator with full system access", true);
        createRole("TENANT_ADMIN", "Tenant-level administrator", true);
        createRole("CLAIMS_MANAGER", "Manages claims processing workflows", true);
        createRole("CLAIMS_PROCESSOR", "Processes individual claims", true);
        createRole("CLAIMS_VIEWER", "Read-only access to claims", true);
        createRole("MEMBER_MANAGER", "Manages member records", true);
        createRole("MEMBER_VIEWER", "Read-only access to member records", true);
        createRole("BATCH_OPERATOR", "Operates batch processing jobs", true);
        createRole("REPORT_VIEWER", "Views reports and analytics", true);
    }

    private void seedPrivileges() {
        createPrivilege("claims:read", "Read claims data", "claims", "read");
        createPrivilege("claims:write", "Create and update claims", "claims", "write");
        createPrivilege("claims:delete", "Delete claims", "claims", "delete");
        createPrivilege("claims:admin", "Full claims administration", "claims", "admin");
        createPrivilege("members:read", "Read member data", "members", "read");
        createPrivilege("members:write", "Create and update members", "members", "write");
        createPrivilege("members:delete", "Delete members", "members", "delete");
        createPrivilege("batch:execute", "Execute batch jobs", "batch", "execute");
        createPrivilege("batch:view", "View batch job status", "batch", "view");
        createPrivilege("reports:view", "View reports", "reports", "view");
        createPrivilege("reports:export", "Export reports", "reports", "export");
        createPrivilege("entitlements:admin", "Manage entitlements", "entitlements", "admin");
        createPrivilege("tenants:admin", "Manage tenants", "tenants", "admin");
    }

    private void createRole(String name, String description, boolean systemRole) {
        if (!roleRepository.existsByTenantIdAndName(DEFAULT_TENANT_ID, name)) {
            Role role = Role.builder()
                    .tenantId(DEFAULT_TENANT_ID)
                    .name(name)
                    .description(description)
                    .systemRole(systemRole)
                    .build();
            roleRepository.save(role);
            log.debug("Created role: {}", name);
        }
    }

    private void createPrivilege(String name, String description, String resource, String action) {
        if (!privilegeRepository.existsByTenantIdAndName(DEFAULT_TENANT_ID, name)) {
            Privilege privilege = Privilege.builder()
                    .tenantId(DEFAULT_TENANT_ID)
                    .name(name)
                    .description(description)
                    .resource(resource)
                    .action(action)
                    .build();
            privilegeRepository.save(privilege);
            log.debug("Created privilege: {}", name);
        }
    }
}
