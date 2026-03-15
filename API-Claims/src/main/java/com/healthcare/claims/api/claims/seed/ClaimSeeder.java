package com.healthcare.claims.api.claims.seed;

import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import com.healthcare.claims.api.claims.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClaimSeeder implements CommandLineRunner {

    private final ClaimRepository claimRepository;

    @Override
    public void run(String... args) {
        if (claimRepository.count() > 0) {
            log.info("Claims already seeded, skipping. Count: {}", claimRepository.count());
            return;
        }
        log.info("Seeding sample claims...");
        seedClaims();
        log.info("Seeded {} claims", claimRepository.count());
    }

    private void seedClaims() {
        // TNT-001 = "Horizon Health Partners" (matches API-Tenants TenantSeeder)
        // MBR-xxx IDs match API-Members MemberSeeder
        String tenantId = "TNT-001";

        createClaim(tenantId, "MBR-001", "CLM-2026-0001", ClaimStage.APPROVED,
                "1245.00", "0.9710", "AUTO_APPROVED: High confidence score exceeded threshold", 14);
        createClaim(tenantId, "MBR-002", "CLM-2026-0002", ClaimStage.ADJUDICATION_REVIEW,
                "3780.00", "0.8210", null, 10);
        createClaim(tenantId, "MBR-003", "CLM-2026-0003", ClaimStage.EXTRACTION_REVIEW,
                "892.50", "0.9650", null, 7);
        createClaim(tenantId, "MBR-001", "CLM-2026-0004", ClaimStage.DENIED,
                "5200.00", "0.4530", "DENIED: Low confidence, possible duplicate claim detected", 12);
        createClaim(tenantId, "MBR-004", "CLM-2026-0005", ClaimStage.DATA_EXTRACTION,
                "1890.00", null, null, 5);
        createClaim(tenantId, "MBR-005", "CLM-2026-0006", ClaimStage.INTAKE_RECEIVED,
                "2340.00", null, null, 2);
        createClaim(tenantId, "MBR-002", "CLM-2026-0007", ClaimStage.SETTLEMENT,
                "1560.00", "0.9910", "AUTO_APPROVED: High confidence score exceeded threshold", 13);
        createClaim(tenantId, "MBR-003", "CLM-2026-0008", ClaimStage.ELIGIBILITY_CHECK,
                "4120.00", "0.8870", null, 6);
        createClaim(tenantId, "MBR-006", "CLM-2026-0009", ClaimStage.CLOSED,
                "780.00", "0.9950", "AUTO_APPROVED: High confidence score exceeded threshold", 20);
        createClaim(tenantId, "MBR-004", "CLM-2026-0010", ClaimStage.ADJUDICATION,
                "3100.00", "0.9120", null, 8);
    }

    private void createClaim(String tenantId, String customerId, String claimNumber,
                             ClaimStage stage, String amount, String confidence,
                             String adjudicationResult, int daysAgo) {
        Claim.ClaimBuilder builder = Claim.builder()
                .tenantId(tenantId)
                .customerId(customerId)
                .claimNumber(claimNumber)
                .stage(stage)
                .submittedDate(LocalDate.now().minusDays(daysAgo));

        if (confidence != null) {
            builder.confidenceScore(new BigDecimal(confidence));
        }
        if (adjudicationResult != null) {
            builder.adjudicationResult(adjudicationResult);
        }

        claimRepository.save(builder.build());
    }
}
