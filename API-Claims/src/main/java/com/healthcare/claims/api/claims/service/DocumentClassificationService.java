package com.healthcare.claims.api.claims.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentClassificationService {

    public enum DocumentType {
        CMS_1500, UB_04, EOB, ITEMIZED_BILL, LAB_REPORT, PRESCRIPTION, REFERRAL, UNKNOWN
    }

    /**
     * Classify document based on filename extension and content type.
     * In production, this would use AI (Bedrock Data Automation / Vertex AI).
     */
    public DocumentType classify(String fileName, String contentType) {
        String lower = fileName.toLowerCase();
        if (lower.contains("cms") || lower.contains("1500")) return DocumentType.CMS_1500;
        if (lower.contains("ub04") || lower.contains("ub-04")) return DocumentType.UB_04;
        if (lower.contains("eob") || lower.contains("explanation")) return DocumentType.EOB;
        if (lower.contains("bill") || lower.contains("invoice")) return DocumentType.ITEMIZED_BILL;
        if (lower.contains("lab") || lower.contains("test") || lower.contains("result")) return DocumentType.LAB_REPORT;
        if (lower.contains("rx") || lower.contains("prescription") || lower.contains("pharmacy")) return DocumentType.PRESCRIPTION;
        if (lower.contains("referral") || lower.contains("auth")) return DocumentType.REFERRAL;

        // Classify by content type
        if (contentType != null) {
            if (contentType.contains("pdf")) return DocumentType.CMS_1500; // default PDF to claim form
            if (contentType.contains("image")) return DocumentType.CMS_1500; // default image to claim form
        }

        return DocumentType.UNKNOWN;
    }
}
