package com.healthcare.claims.api.tenants.command;

import com.healthcare.claims.api.tenants.model.Tenant;
import com.healthcare.claims.api.tenants.model.TenantStatus;
import com.healthcare.claims.api.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantCommandHandler {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant handleCreate(CreateTenantCommand command) {
        log.info("Creating tenant with tenantId: {}", command.getTenantId());

        if (tenantRepository.findByTenantId(command.getTenantId()).isPresent()) {
            throw new IllegalArgumentException("Tenant with ID " + command.getTenantId() + " already exists");
        }

        Tenant tenant = Tenant.builder()
                .tenantId(command.getTenantId())
                .name(command.getName())
                .displayName(command.getDisplayName() != null ? command.getDisplayName() : command.getName())
                .domain(command.getDomain())
                .status(TenantStatus.PENDING_ACTIVATION)
                .plan(command.getPlan() != null ? command.getPlan() : "starter")
                .contactEmail(command.getContactEmail())
                .contactPhone(command.getContactPhone())
                .address(command.getAddress())
                .maxUsers(command.getMaxUsers())
                .build();

        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant created successfully: {} ({})", saved.getName(), saved.getTenantId());
        return saved;
    }

    @Transactional
    public Tenant handleUpdate(UUID id, UpdateTenantCommand command) {
        log.info("Updating tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        if (command.getName() != null) {
            tenant.setName(command.getName());
        }
        if (command.getDisplayName() != null) {
            tenant.setDisplayName(command.getDisplayName());
        }
        if (command.getDomain() != null) {
            tenant.setDomain(command.getDomain());
        }
        if (command.getPlan() != null) {
            tenant.setPlan(command.getPlan());
        }
        if (command.getContactEmail() != null) {
            tenant.setContactEmail(command.getContactEmail());
        }
        if (command.getContactPhone() != null) {
            tenant.setContactPhone(command.getContactPhone());
        }
        if (command.getAddress() != null) {
            tenant.setAddress(command.getAddress());
        }
        if (command.getMaxUsers() != null) {
            tenant.setMaxUsers(command.getMaxUsers());
        }

        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant updated successfully: {} ({})", saved.getName(), saved.getTenantId());
        return saved;
    }

    @Transactional
    public void handleDelete(UUID id) {
        log.info("Deleting tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        tenantRepository.delete(tenant);
        log.info("Tenant deleted successfully: {} ({})", tenant.getName(), tenant.getTenantId());
    }

    @Transactional
    public Tenant handleActivate(UUID id) {
        log.info("Activating tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        tenant.setStatus(TenantStatus.ACTIVE);
        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant activated: {} ({})", saved.getName(), saved.getTenantId());
        return saved;
    }

    @Transactional
    public Tenant handleSuspend(UUID id) {
        log.info("Suspending tenant with id: {}", id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        tenant.setStatus(TenantStatus.SUSPENDED);
        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant suspended: {} ({})", saved.getName(), saved.getTenantId());
        return saved;
    }

    @Transactional
    public Tenant handleStatusChange(UUID id, TenantStatus status) {
        log.info("Changing status of tenant {} to {}", id, status);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id: " + id));

        tenant.setStatus(status);
        Tenant saved = tenantRepository.save(tenant);
        log.info("Tenant status changed to {}: {} ({})", status, saved.getName(), saved.getTenantId());
        return saved;
    }
}
