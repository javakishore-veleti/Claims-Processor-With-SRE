package com.healthcare.claims.api.entitlements.command;

import com.healthcare.claims.api.entitlements.model.User;
import com.healthcare.claims.api.entitlements.model.UserGroup;
import com.healthcare.claims.api.entitlements.model.UserStatus;
import com.healthcare.claims.api.entitlements.repository.GroupRepository;
import com.healthcare.claims.api.entitlements.repository.UserGroupRepository;
import com.healthcare.claims.api.entitlements.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandHandler {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(CreateUserCommand command) {
        log.info("Creating user: {} for tenant: {}", command.getUsername(), command.getTenantId());

        if (userRepository.existsByTenantIdAndUsername(command.getTenantId(), command.getUsername())) {
            throw new IllegalArgumentException("Username already exists for this tenant: " + command.getUsername());
        }

        if (userRepository.existsByTenantIdAndEmail(command.getTenantId(), command.getEmail())) {
            throw new IllegalArgumentException("Email already exists for this tenant: " + command.getEmail());
        }

        User user = User.builder()
                .tenantId(command.getTenantId())
                .username(command.getUsername())
                .email(command.getEmail())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .passwordHash(passwordEncoder.encode(command.getPassword()))
                .phone(command.getPhone())
                .status(UserStatus.PENDING_ACTIVATION)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UpdateUserCommand command) {
        log.info("Updating user: {} for tenant: {}", command.getId(), command.getTenantId());

        User user = userRepository.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getId()));

        if (command.getEmail() != null) {
            user.setEmail(command.getEmail());
        }
        if (command.getFirstName() != null) {
            user.setFirstName(command.getFirstName());
        }
        if (command.getLastName() != null) {
            user.setLastName(command.getLastName());
        }
        if (command.getPhone() != null) {
            user.setPhone(command.getPhone());
        }
        if (command.getStatus() != null) {
            user.setStatus(command.getStatus());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String tenantId, UUID userId) {
        log.info("Deleting user: {} for tenant: {}", userId, tenantId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!user.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("User does not belong to tenant: " + tenantId);
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserGroup assignToGroup(AssignUserToGroupCommand command) {
        log.info("Assigning user: {} to group: {} for tenant: {}",
                command.getUserId(), command.getGroupId(), command.getTenantId());

        if (userGroupRepository.existsByTenantIdAndUserIdAndGroupId(
                command.getTenantId(), command.getUserId(), command.getGroupId())) {
            throw new IllegalArgumentException("User is already assigned to this group");
        }

        var user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.getUserId()));

        var group = groupRepository.findById(command.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + command.getGroupId()));

        UserGroup userGroup = UserGroup.builder()
                .tenantId(command.getTenantId())
                .user(user)
                .group(group)
                .build();

        return userGroupRepository.save(userGroup);
    }
}
