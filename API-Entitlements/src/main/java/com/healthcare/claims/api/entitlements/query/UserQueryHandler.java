package com.healthcare.claims.api.entitlements.query;

import com.healthcare.claims.api.entitlements.model.User;
import com.healthcare.claims.api.entitlements.repository.UserRepository;
import com.healthcare.claims.common.entitlements.dto.UserRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserQueryHandler {

    private final UserRepository userRepository;

    public UserRespDTO findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toResponse(user);
    }

    public UserRespDTO findByUsername(String tenantId, String username) {
        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return toResponse(user);
    }

    public List<UserRespDTO> search(String tenantId, String query) {
        return userRepository.searchByTenantId(tenantId, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserRespDTO> listByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserRespDTO toResponse(User user) {
        return UserRespDTO.builder()
                .id(user.getId() != null ? user.getId().toString() : null)
                .tenantId(user.getTenantId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .lastLoginAt(user.getLastLoginAt() != null
                        ? user.getLastLoginAt().toString() : null)
                .createdAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().toString() : null)
                .updatedAt(user.getUpdatedAt() != null
                        ? user.getUpdatedAt().toString() : null)
                .build();
    }
}
