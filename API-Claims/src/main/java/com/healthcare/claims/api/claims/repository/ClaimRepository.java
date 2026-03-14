package com.healthcare.claims.api.claims.repository;

import com.healthcare.claims.api.claims.model.Claim;
import com.healthcare.claims.api.claims.model.ClaimStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    Page<Claim> findByCustomerId(String customerId, Pageable pageable);

    Page<Claim> findByStage(ClaimStage stage, Pageable pageable);

    Page<Claim> findByCustomerIdAndStage(String customerId, ClaimStage stage, Pageable pageable);

    @Query("SELECT c FROM Claim c WHERE "
            + "(:customerId IS NULL OR c.customerId = :customerId) AND "
            + "(:stage IS NULL OR c.stage = :stage) AND "
            + "(:fromDate IS NULL OR c.submittedDate >= :fromDate) AND "
            + "(:toDate IS NULL OR c.submittedDate <= :toDate)")
    Page<Claim> searchClaims(
            @Param("customerId") String customerId,
            @Param("stage") ClaimStage stage,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
}
