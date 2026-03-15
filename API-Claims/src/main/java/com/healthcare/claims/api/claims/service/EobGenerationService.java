package com.healthcare.claims.api.claims.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class EobGenerationService {

    /**
     * Generate Explanation of Benefits.
     * In production, generates a PDF with claim details, covered amounts, member responsibility.
     */
    public Map<String, Object> generateEob(String claimNumber, String memberName,
            double billedAmount, double allowedAmount, double copay, double deductible) {
        log.info("Generating EOB for claim {}", claimNumber);

        double memberResponsibility = copay + deductible;
        double planPays = allowedAmount - memberResponsibility;

        return Map.of(
            "claimNumber", claimNumber,
            "memberName", memberName,
            "billedAmount", billedAmount,
            "allowedAmount", allowedAmount,
            "copay", copay,
            "deductible", deductible,
            "planPays", Math.max(0, planPays),
            "memberResponsibility", memberResponsibility,
            "generatedAt", java.time.LocalDateTime.now().toString(),
            "status", "GENERATED"
        );
    }
}
