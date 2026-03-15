package com.healthcare.claims.api.claims.service;

import com.healthcare.claims.api.claims.model.ClaimStage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClaimWorkflowServiceTest {

    @Test
    void validTransitionFromIntakeToExtraction() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.INTAKE_RECEIVED, ClaimStage.DATA_EXTRACTION));
    }

    @Test
    void validTransitionFromIntakeToDocumentVerification() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.INTAKE_RECEIVED, ClaimStage.DOCUMENT_VERIFICATION));
    }

    @Test
    void invalidTransitionFromIntakeToApproved() {
        ClaimWorkflowService service = createMinimalService();
        assertFalse(service.isValidTransition(ClaimStage.INTAKE_RECEIVED, ClaimStage.APPROVED));
    }

    @Test
    void validTransitionFromAdjudicationToApproved() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.ADJUDICATION, ClaimStage.APPROVED));
    }

    @Test
    void validTransitionFromAdjudicationToDenied() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.ADJUDICATION, ClaimStage.DENIED));
    }

    @Test
    void validTransitionFromAdjudicationToPartialApproved() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.ADJUDICATION, ClaimStage.PARTIAL_APPROVED));
    }

    @Test
    void validTransitionFromDeniedToAppeal() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.DENIED, ClaimStage.APPEAL));
    }

    @Test
    void invalidTransitionFromClosedToAnything() {
        ClaimWorkflowService service = createMinimalService();
        assertFalse(service.isValidTransition(ClaimStage.CLOSED, ClaimStage.APPEAL));
        assertFalse(service.isValidTransition(ClaimStage.CLOSED, ClaimStage.APPROVED));
    }

    @Test
    void validTransitionFromApprovedToSettlement() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.APPROVED, ClaimStage.SETTLEMENT));
    }

    @Test
    void validTransitionFromSettlementToClosed() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.SETTLEMENT, ClaimStage.CLOSED));
    }

    @Test
    void validTransitionFromAppealToAdjudicationReview() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.APPEAL, ClaimStage.ADJUDICATION_REVIEW));
    }

    @Test
    void invalidTransitionFromApprovedToIntake() {
        ClaimWorkflowService service = createMinimalService();
        assertFalse(service.isValidTransition(ClaimStage.APPROVED, ClaimStage.INTAKE_RECEIVED));
    }

    @Test
    void validTransitionFromExtractionReviewToEligibilityCheck() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.EXTRACTION_REVIEW, ClaimStage.ELIGIBILITY_CHECK));
    }

    @Test
    void validTransitionFromEligibilityCheckToAdjudication() {
        ClaimWorkflowService service = createMinimalService();
        assertTrue(service.isValidTransition(ClaimStage.ELIGIBILITY_CHECK, ClaimStage.ADJUDICATION));
    }

    private ClaimWorkflowService createMinimalService() {
        return new ClaimWorkflowService(null, null, null, null);
    }
}
