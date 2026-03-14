package com.healthcare.claims.api.entitlements.query;

import com.healthcare.claims.api.entitlements.model.*;
import com.healthcare.claims.api.entitlements.repository.*;
import com.healthcare.claims.common.entitlements.dto.GroupRespDTO;
import com.healthcare.claims.common.entitlements.dto.PrivilegeRespDTO;
import com.healthcare.claims.common.entitlements.dto.RoleRespDTO;
import com.healthcare.claims.common.entitlements.dto.UserEntitlementRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EntitlementQueryHandler {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final GroupRolePrivilegeRepository groupRolePrivilegeRepository;
    private final UserRolePrivilegeRepository userRolePrivilegeRepository;

    public UserEntitlementRespDTO getUserPrivileges(String tenantId, UUID userId) {
        log.info("Getting privileges for user: {} in tenant: {}", userId, tenantId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Get groups for user
        List<UserGroup> userGroups = userGroupRepository.findByTenantIdAndUserId(tenantId, userId);
        List<GroupRespDTO> groups = userGroups.stream()
                .map(ug -> toGroupResponse(ug.getGroup()))
                .collect(Collectors.toList());

        // Get group-level roles and privileges
        List<RoleRespDTO> roles = new ArrayList<>();
        List<PrivilegeRespDTO> groupPrivileges = new ArrayList<>();

        for (UserGroup ug : userGroups) {
            List<GroupRole> groupRoles = groupRoleRepository.findByTenantIdAndGroupId(tenantId, ug.getGroup().getId());
            for (GroupRole gr : groupRoles) {
                roles.add(toRoleResponse(gr.getRole()));

                List<GroupRolePrivilege> grPrivileges = groupRolePrivilegeRepository
                        .findByTenantIdAndGroupRoleId(tenantId, gr.getId());
                for (GroupRolePrivilege grp : grPrivileges) {
                    groupPrivileges.add(toPrivilegeResponse(grp.getPrivilege()));
                }
            }
        }

        // Get user-specific privileges
        List<UserRolePrivilege> userRolePrivileges = userRolePrivilegeRepository.findByTenantIdAndUserId(tenantId, userId);
        List<PrivilegeRespDTO> userSpecificPrivileges = userRolePrivileges.stream()
                .map(urp -> toPrivilegeResponse(urp.getPrivilege()))
                .collect(Collectors.toList());

        // Also add user-specific roles
        userRolePrivileges.stream()
                .map(urp -> toRoleResponse(urp.getRole()))
                .forEach(roles::add);

        // Combine and deduplicate effective privileges
        List<PrivilegeRespDTO> effectivePrivileges = Stream.concat(
                        groupPrivileges.stream(),
                        userSpecificPrivileges.stream())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(PrivilegeRespDTO::getId, p -> p, (p1, p2) -> p1),
                        map -> new ArrayList<>(map.values())));

        // Deduplicate roles
        roles = roles.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(RoleRespDTO::getId, r -> r, (r1, r2) -> r1),
                        map -> new ArrayList<>(map.values())));

        return UserEntitlementRespDTO.builder()
                .userId(userId.toString())
                .username(user.getUsername())
                .groups(groups)
                .roles(roles)
                .privileges(effectivePrivileges)
                .build();
    }

    public List<RoleRespDTO> getRolesForUser(String tenantId, UUID userId) {
        UserEntitlementRespDTO entitlements = getUserPrivileges(tenantId, userId);
        return entitlements.getRoles();
    }

    public List<GroupRespDTO> getGroupsForUser(String tenantId, UUID userId) {
        return userGroupRepository.findByTenantIdAndUserId(tenantId, userId)
                .stream()
                .map(ug -> toGroupResponse(ug.getGroup()))
                .collect(Collectors.toList());
    }

    private GroupRespDTO toGroupResponse(Group group) {
        return GroupRespDTO.builder()
                .id(group.getId() != null ? group.getId().toString() : null)
                .tenantId(group.getTenantId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }

    private RoleRespDTO toRoleResponse(Role role) {
        return RoleRespDTO.builder()
                .id(role.getId() != null ? role.getId().toString() : null)
                .tenantId(role.getTenantId())
                .name(role.getName())
                .description(role.getDescription())
                .systemRole(role.isSystemRole())
                .build();
    }

    private PrivilegeRespDTO toPrivilegeResponse(Privilege privilege) {
        return PrivilegeRespDTO.builder()
                .id(privilege.getId() != null ? privilege.getId().toString() : null)
                .tenantId(privilege.getTenantId())
                .name(privilege.getName())
                .description(privilege.getDescription())
                .resource(privilege.getResource())
                .action(privilege.getAction())
                .build();
    }
}
