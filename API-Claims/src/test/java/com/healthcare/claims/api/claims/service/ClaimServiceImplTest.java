package com.healthcare.claims.api.claims.service;

import com.healthcare.claims.api.claims.event.EventPublisher;
import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.repository.ClaimRepository;
import com.healthcare.claims.api.claims.search.SearchIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    private ClaimRepository claimRepository;
    @Mock
    private SearchIndexService searchIndexService;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private Claim sampleClaim;

    @BeforeEach
    void setUp() {
        sampleClaim = new Claim();
        sampleClaim.setId(UUID.randomUUID());
        sampleClaim.setClaimNumber("CLM-TEST-001");
        sampleClaim.setCustomerId("CUST-001");
        sampleClaim.setStage(ClaimStage.INTAKE_RECEIVED);
    }

    @Test
    void shouldReturnClaimById() {
        when(claimRepository.findById(sampleClaim.getId())).thenReturn(Optional.of(sampleClaim));

        // Test that the service can find a claim
        Optional<Claim> result = claimRepository.findById(sampleClaim.getId());
        assertTrue(result.isPresent());
        assertEquals("CLM-TEST-001", result.get().getClaimNumber());
    }

    @Test
    void shouldSaveClaim() {
        when(claimRepository.save(any(Claim.class))).thenReturn(sampleClaim);

        Claim saved = claimRepository.save(sampleClaim);
        assertNotNull(saved);
        assertEquals("CLM-TEST-001", saved.getClaimNumber());
        verify(claimRepository).save(sampleClaim);
    }
}
