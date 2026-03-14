package com.healthcare.claims.api.entitlements.command;

import com.healthcare.claims.api.entitlements.model.*;
import com.healthcare.claims.api.entitlements.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntitlementCommandHandler {

    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final GroupRolePrivilegeRepository groupRolePrivilegeRepository;
    private final UserRolePrivilegeRepository userRolePrivilegeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Role createRole(String tenantId, String name, String description, boolean systemRole) {
        log.info("Creating role: {} for tenant: {}", name, tenantId);

        if (roleRepository.existsByTenantIdAndName(tenantId, name)) {
            throw new IllegalArgumentException("Role already exists: " + name);
        }

        Role role = Role.builder()
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .systemRole(systemRole)
                .build();

        return roleRepository.save(role);
    }

    @Transactional
    public Privilege createPrivilege(String tenantId, String name, String description, String resource, String action) {
        log.info("Creating privilege: {} for tenant: {}", name, tenantId);

        if (privilegeRepository.existsByTenantIdAndName(tenantId, name)) {
            throw new IllegalArgumentException("Privilege already exists: " + name);
        }

        Privilege privilege = Privilege.builder()
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .resource(resource)
                .action(action)
                .build();

        return privilegeRepository.save(privilege);
    }

    @Transactional
    public Group createGroup(String tenantId, String name, String description) {
        log.info("Creating group: {} for tenant: {}", name, tenantId);

        if (groupRepository.existsByTenantIdAndName(tenantId, name)) {
            throw new IllegalArgumentException("Group already exists: " + name);
        }

        Group group = Group.builder()
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .build();

        return groupRepository.save(group);
    }

    @Transactional
    public GroupRole assignRoleToGroup(AssignRoleToGroupCommand command) {
        log.info("Assigning role: {} to group: {} for tenant: {}",
                command.getRoleId(), command.getGroupId(), command.getTenantId());

        if (groupRoleRepository.existsByTenantIdAndGroupIdAndRoleId(
                command.getTenantId(), command.getGroupId(), command.getRoleId())) {
            throw new IllegalArgumentException("Role is already assigned to this group");
        }

        var group = groupRepository.findById(command.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.getGroupId()));

        var role = roleRepository.findById(command.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.getRoleId()));

        GroupRole groupRole = GroupRole.builder()
                .tenantId(command.getTenantId())
                .group(group)
                .role(role)
                .build();

        return groupRoleRepository.save(groupRole);
    }

    @Transactional
    public GroupRolePrivilege assignPrivilegeToGroupRole(AssignPrivilegeToGroupRoleCommand command) {
        log.info("Assigning privilege: {} to group-role: {} for tenant: {}",
                command.getPrivilegeId(), command.getGroupRoleId(), command.getTenantId());

        if (groupRolePrivilegeRepository.existsByTenantIdAndGroupRoleIdAndPrivilegeId(
                command.getTenantId(), command.getGroupRoleId(), command.getPrivilegeId())) {
            throw new IllegalArgumentException("Privilege is already assigned to this group-role");
        }

        var groupRole = groupRoleRepository.findById(command.getGroupRoleId())
                .orElseThrow(() -> new IllegalArgumentException("GroupRole not found: " + command.getGroupRoleId()));

        var privilege = privilegeRepository.findById(command.getPrivilegeId())
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found: " + command.getPrivilegeId()));

        GroupRolePrivilege grp = GroupRolePrivilege.builder()
                .tenantId(command.getTenantId())
                .groupRole(groupRole)
                .privilege(privilege)
                .build();

        return groupRolePrivilegeRepository.save(grp);
    }

    @Transactional
    public UserRolePrivilege assignPrivilegeToUser(AssignPrivilegeToUserCommand command) {
        log.info("Assigning privilege: {} with role: {} to user: {} for tenant: {}",
                command.getPrivilegeId(), command.getRoleId(), command.getUserId(), command.getTenantId());

        if (userRolePrivilegeRepository.existsByTenantIdAndUserIdAndRoleIdAndPrivilegeId(
                command.getTenantId(), command.getUserId(), command.getRoleId(), command.getPrivilegeId())) {
            throw new IllegalArgumentException("User already has this role-privilege assignment");
        }

        var user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        var role = roleRepository.findById(command.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.getRoleId()));

        var privilege = privilegeRepository.findById(command.getPrivilegeId())
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found: " + command.getPrivilegeId()));

        UserRolePrivilege urp = UserRolePrivilege.builder()
                .tenantId(command.getTenantId())
                .user(user)
                .role(role)
                .privilege(privilege)
                .build();

        return userRolePrivilegeRepository.save(urp);
    }

    @Transactional
    public void deleteRole(String tenantId, UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));
        if (!role.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Role does not belong to tenant: " + tenantId);
        }
        roleRepository.delete(role);
    }

    @Transactional
    public void deletePrivilege(String tenantId, UUID privilegeId) {
        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new IllegalArgumentException("Privilege not found: " + privilegeId));
        if (!privilege.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Privilege does not belong to tenant: " + tenantId);
        }
        privilegeRepository.delete(privilege);
    }

    @Transactional
    public void deleteGroup(String tenantId, UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
        if (!group.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Group does not belong to tenant: " + tenantId);
        }
        groupRepository.delete(group);
    }
}
