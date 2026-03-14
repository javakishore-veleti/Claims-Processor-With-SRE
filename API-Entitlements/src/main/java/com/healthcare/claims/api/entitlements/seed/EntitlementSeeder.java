package com.healthcare.claims.api.entitlements.seed;

import com.healthcare.claims.api.entitlements.model.*;
import com.healthcare.claims.api.entitlements.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntitlementSeeder implements CommandLineRunner {

    private static final String DEFAULT_TENANT_ID = "default-tenant";

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.findByTenantId(DEFAULT_TENANT_ID).isEmpty()) {
            log.info("Seeding default roles, privileges, and users for tenant: {}", DEFAULT_TENANT_ID);
            seedRoles();
            seedPrivileges();
            seedGroupsAndUsers();
            log.info("Seeding complete");
            logSeedCredentials();
        } else {
            log.info("Roles already exist for tenant: {}, skipping seed", DEFAULT_TENANT_ID);
            logSeedCredentials();
        }
    }

    private void logSeedCredentials() {
        log.info("==========================================================");
        log.info("  DEFAULT DEV CREDENTIALS (seeded users)");
        log.info("==========================================================");
        log.info("  admin    / admin123    — SUPER_ADMIN (full access)");
        log.info("  manager  / manager123  — CLAIMS_MANAGER");
        log.info("  processor/ processor123— CLAIMS_PROCESSOR");
        log.info("  viewer   / viewer123   — CLAIMS_VIEWER");
        log.info("  member   / member123   — MEMBER_MANAGER");
        log.info("  batch    / batch123    — BATCH_OPERATOR");
        log.info("  Tenant: {}", DEFAULT_TENANT_ID);
        log.info("==========================================================");
    }

    private void seedGroupsAndUsers() {
        // Create default groups
        Group adminsGroup = createGroup("Administrators", "Full system access group");
        Group claimsGroup = createGroup("Claims Team", "Claims processing team");
        Group membersGroup = createGroup("Members Team", "Member management team");
        Group opsGroup = createGroup("Operations", "Batch and reporting operations");

        // Create seed users with known passwords
        User admin = createUser("admin", "admin@claims-processor.local", "Admin", "User", "admin123");
        User manager = createUser("manager", "manager@claims-processor.local", "Claims", "Manager", "manager123");
        User processor = createUser("processor", "processor@claims-processor.local", "Claims", "Processor", "processor123");
        User viewer = createUser("viewer", "viewer@claims-processor.local", "Claims", "Viewer", "viewer123");
        User memberMgr = createUser("member", "member@claims-processor.local", "Member", "Manager", "member123");
        User batchOp = createUser("batch", "batch@claims-processor.local", "Batch", "Operator", "batch123");

        // Assign users to groups
        if (admin != null && adminsGroup != null) assignToGroup(admin, adminsGroup);
        if (manager != null && claimsGroup != null) assignToGroup(manager, claimsGroup);
        if (processor != null && claimsGroup != null) assignToGroup(processor, claimsGroup);
        if (viewer != null && claimsGroup != null) assignToGroup(viewer, claimsGroup);
        if (memberMgr != null && membersGroup != null) assignToGroup(memberMgr, membersGroup);
        if (batchOp != null && opsGroup != null) assignToGroup(batchOp, opsGroup);
    }

    private Group createGroup(String name, String description) {
        List<Group> existing = groupRepository.findByTenantId(DEFAULT_TENANT_ID);
        for (Group g : existing) {
            if (g.getName().equals(name)) return g;
        }
        Group group = Group.builder()
                .tenantId(DEFAULT_TENANT_ID)
                .name(name)
                .description(description)
                .build();
        group = groupRepository.save(group);
        log.debug("Created group: {}", name);
        return group;
    }

    private User createUser(String username, String email, String firstName, String lastName, String rawPassword) {
        if (userRepository.findByTenantIdAndUsername(DEFAULT_TENANT_ID, username).isPresent()) {
            return userRepository.findByTenantIdAndUsername(DEFAULT_TENANT_ID, username).get();
        }
        User user = User.builder()
                .tenantId(DEFAULT_TENANT_ID)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);
        log.debug("Created user: {} (password: {})", username, rawPassword);
        return user;
    }

    private void assignToGroup(User user, Group group) {
        UserGroup ug = UserGroup.builder()
                .tenantId(DEFAULT_TENANT_ID)
                .userId(user.getId())
                .groupId(group.getId())
                .build();
        userGroupRepository.save(ug);
        log.debug("Assigned user {} to group {}", user.getUsername(), group.getName());
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
