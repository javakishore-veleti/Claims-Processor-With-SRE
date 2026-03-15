package com.healthcare.claims.api.claims.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AiExtractionService {

    /**
     * Mock AI extraction. In production, this calls:
     * - AWS Bedrock Data Automation
     * - GCP Vertex AI
     * - Azure AI Document Intelligence
     * Based on feature toggle: claims.features.ai.provider
     */
    public Map<String, Object> extractData(String documentType, String storageKey) {
        log.info("AI extraction started for document: {} (type: {})", storageKey, documentType);

        // Simulate extraction with mock data
        Map<String, Object> extracted = new LinkedHashMap<>();
        extracted.put("documentType", documentType);
        extracted.put("extractionTimestamp", java.time.LocalDateTime.now().toString());
        extracted.put("confidenceScore", 0.85 + Math.random() * 0.14); // 85-99%

        // Common fields
        extracted.put("patientName", "Extracted Patient Name");
        extracted.put("patientDob", "1985-06-15");
        extracted.put("memberId", "MBR-" + (100000 + new Random().nextInt(900000)));
        extracted.put("providerName", "Extracted Provider Name");
        extracted.put("providerNpi", "1234567890");
        extracted.put("dateOfService", "2026-03-10");
        extracted.put("placeOfService", "Office");

        // Diagnosis and procedure codes
        extracted.put("diagnosisCodes", List.of("J06.9", "R05.9"));
        extracted.put("procedureCodes", List.of("99213", "87880"));

        // Amounts
        extracted.put("billedAmount", 250.00 + new Random().nextInt(5000));
        extracted.put("allowedAmount", 200.00 + new Random().nextInt(3000));
        extracted.put("copay", 25.00);
        extracted.put("deductible", 100.00);

        // Visual grounding (mock bounding boxes)
        extracted.put("boundingBoxes", Map.of(
            "patientName", Map.of("x", 50, "y", 120, "width", 200, "height", 20),
            "billedAmount", Map.of("x", 400, "y", 350, "width", 100, "height", 20)
        ));

        log.info("AI extraction completed for: {} (confidence: {})", storageKey, extracted.get("confidenceScore"));
        return extracted;
    }
}
