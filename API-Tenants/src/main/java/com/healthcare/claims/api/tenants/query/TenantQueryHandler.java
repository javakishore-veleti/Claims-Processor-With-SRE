package com.healthcare.claims.api.tenants.query;

import com.healthcare.claims.api.tenants.model.Tenant;
import com.healthcare.claims.api.tenants.model.TenantStatus;
import com.healthcare.claims.api.tenants.repository.TenantRepository;
import com.healthcare.claims.common.tenants.dto.TenantRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TenantQueryHandler {

    private final TenantRepository tenantRepository;

    public TenantRespDTO findById(UUID id) {
        log.debug("Finding tenant by id: {}", id);
        return tenantRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));
    }

    public TenantRespDTO findByTenantId(String tenantId) {
        log.debug("Finding tenant by tenantId: {}", tenantId);
        return tenantRepository.findByTenantId(tenantId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with tenantId: " + tenantId));
    }

    public List<TenantRespDTO> search(String name) {
        log.debug("Searching tenants by name containing: {}", name);
        return tenantRepository.searchByNameContaining(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Page<TenantRespDTO> listAll(Pageable pageable) {
        log.debug("Listing all tenants, page: {}", pageable.getPageNumber());
        return tenantRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public List<TenantRespDTO> findActive() {
        log.debug("Finding all active tenants");
        return tenantRepository.findByStatus(TenantStatus.ACTIVE).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TenantRespDTO toResponse(Tenant tenant) {
        return TenantRespDTO.builder()
                .id(tenant.getId() != null ? tenant.getId().toString() : null)
                .tenantId(tenant.getTenantId())
                .name(tenant.getName())
                .displayName(tenant.getDisplayName())
                .domain(tenant.getDomain())
                .status(tenant.getStatus() != null ? tenant.getStatus().name() : null)
                .plan(tenant.getPlan())
                .contactEmail(tenant.getContactEmail())
                .contactPhone(tenant.getContactPhone())
                .address(tenant.getAddress())
                .maxUsers(tenant.getMaxUsers())
                .createdAt(tenant.getCreatedAt() != null
                        ? tenant.getCreatedAt().toString() : null)
                .updatedAt(tenant.getUpdatedAt() != null
                        ? tenant.getUpdatedAt().toString() : null)
                .build();
    }
}
