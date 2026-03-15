package com.healthcare.claims.api.entitlements.controller;

import com.healthcare.claims.api.entitlements.command.CreateUserCommand;
import com.healthcare.claims.api.entitlements.command.UserCommandHandler;
import com.healthcare.claims.api.entitlements.model.User;
import com.healthcare.claims.api.entitlements.model.UserRolePrivilege;
import com.healthcare.claims.api.entitlements.repository.UserRepository;
import com.healthcare.claims.api.entitlements.repository.UserRolePrivilegeRepository;
import com.healthcare.claims.api.entitlements.security.JwtService;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserCommandHandler userCommandHandler;
    private final UserRepository userRepository;
    private final UserRolePrivilegeRepository userRolePrivilegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> signup(@Valid @RequestBody CreateUserCommand command) {
        User user = userCommandHandler.createUser(command);
        Map<String, Object> data = Map.of(
                "message", "User registered successfully",
                "userId", encryptionService.encryptObject(user.getId(), IdType.USER),
                "username", user.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody Map<String, String> credentials) {
        String tenantId = credentials.get("tenantId");
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Lookup user roles
        List<UserRolePrivilege> assignments = userRolePrivilegeRepository.findByTenantIdAndUserId(tenantId, user.getId());
        List<String> roles = assignments.stream()
                .map(urp -> urp.getRole().getName())
                .distinct()
                .collect(Collectors.toList());

        // Default role if none assigned
        if (roles.isEmpty()) {
            roles = List.of("USER");
        }

        // Generate JWT token
        String token = jwtService.generateToken(username, tenantId, roles);

        log.info("User logged in: {} for tenant: {}", username, tenantId);

        Map<String, Object> data = Map.of(
                "message", "Login successful",
                "token", token,
                "userId", encryptionService.encryptObject(user.getId(), IdType.USER),
                "username", user.getUsername(),
                "tenantId", encryptionService.encrypt(user.getTenantId(), IdType.TENANT),
                "roles", roles,
                "status", user.getStatus().name(),
                "expiresAt", Instant.now().plusMillis(86400000).toString()
        );
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, String>>> logout() {
        log.info("User logged out");
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "Logout successful")));
    }
}
