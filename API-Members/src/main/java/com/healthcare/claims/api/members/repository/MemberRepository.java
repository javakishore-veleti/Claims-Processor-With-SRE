package com.healthcare.claims.api.members.repository;

import com.healthcare.claims.api.members.model.Member;
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
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByMemberIdAndSsnLast4(String memberId, String ssnLast4);

    Optional<Member> findBySsnLast4(String ssnLast4);

    @Query("SELECT m FROM Member m WHERE " +
            "(:firstName IS NULL OR LOWER(m.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:memberId IS NULL OR m.memberId = :memberId) AND " +
            "(:dateOfBirth IS NULL OR m.dateOfBirth = :dateOfBirth) AND " +
            "(:policyNumber IS NULL OR m.policyNumber = :policyNumber)")
    Page<Member> searchMembers(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("memberId") String memberId,
            @Param("dateOfBirth") LocalDate dateOfBirth,
            @Param("policyNumber") String policyNumber,
            Pageable pageable);

    boolean existsByMemberId(String memberId);
}
