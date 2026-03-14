package com.healthcare.claims.api.entitlements.repository;

import com.healthcare.claims.api.entitlements.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UUID> {

    List<UserGroup> findByTenantIdAndUserId(String tenantId, UUID userId);

    List<UserGroup> findByTenantIdAndGroupId(String tenantId, UUID groupId);

    boolean existsByTenantIdAndUserIdAndGroupId(String tenantId, UUID userId, UUID groupId);
}
