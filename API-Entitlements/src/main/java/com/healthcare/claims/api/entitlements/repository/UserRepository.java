package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.User;
import com.healthcare.claims.api.entitlements.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findByTenantId(String tenantId);

    Optional<User> findByTenantIdAndUsername(String tenantId, String username);

    Optional<User> findByTenantIdAndEmail(String tenantId, String email);

    List<User> findByTenantIdAndStatus(String tenantId, UserStatus status);

    boolean existsByTenantIdAndUsername(String tenantId, String username);

    boolean existsByTenantIdAndEmail(String tenantId, String email);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchByTenantId(@Param("tenantId") String tenantId, @Param("query") String query);
}
