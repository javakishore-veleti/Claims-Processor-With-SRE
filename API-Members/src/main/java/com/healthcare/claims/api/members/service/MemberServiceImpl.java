package com.healthcare.claims.api.members.service;

import com.healthcare.claims.common.members.dto.MemberReqDTO;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import com.healthcare.claims.api.members.model.Member;
import com.healthcare.claims.api.members.event.EventPublisher;
import com.healthcare.claims.api.members.event.MemberEvent;
import com.healthcare.claims.api.members.event.MemberEventType;
import com.healthcare.claims.api.members.repository.MemberRepository;
import com.healthcare.claims.api.members.search.SearchIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final SearchIndexService searchIndexService;
    private final EventPublisher eventPublisher;

    private static final String INDEX_NAME = "members-data";

    @Override
    public MemberRespDTO createMember(MemberReqDTO request) {
        if (memberRepository.existsByMemberId(request.getMemberId())) {
            throw new IllegalArgumentException("Member with ID " + request.getMemberId() + " already exists");
        }

        Member member = mapToEntity(request);
        Member saved = memberRepository.save(member);
        log.info("Created member with ID: {}", saved.getId());

        try {
            searchIndexService.indexDocument(INDEX_NAME, saved.getId().toString(), memberToMap(saved));
        } catch (Exception e) {
            log.warn("Failed to index member {} in search: {}", saved.getId(), e.getMessage());
        }

        publishMemberEvent(MemberEventType.MEMBER_CREATED, saved);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberRespDTO getMemberById(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
        return mapToResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberRespDTO> searchMembers(String firstName, String lastName, String memberId,
                                               LocalDate dateOfBirth, String policyNumber, Pageable pageable) {
        return memberRepository.searchMembers(firstName, lastName, memberId, dateOfBirth, policyNumber, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public MemberRespDTO updateMember(UUID id, MemberReqDTO request) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));

        existing.setMemberId(request.getMemberId());
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setDateOfBirth(request.getDateOfBirth() != null
                ? LocalDate.parse(request.getDateOfBirth()) : null);
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setAddress(request.getAddress());
        existing.setPolicyNumber(request.getPolicyNumber());
        existing.setPolicyStatus(request.getPolicyStatus());
        existing.setSsnLast4(request.getSsnLast4());

        Member updated = memberRepository.save(existing);
        log.info("Updated member with ID: {}", updated.getId());

        try {
            searchIndexService.updateDocument(INDEX_NAME, updated.getId().toString(), memberToMap(updated));
        } catch (Exception e) {
            log.warn("Failed to update member {} in search: {}", updated.getId(), e.getMessage());
        }

        publishMemberEvent(MemberEventType.MEMBER_UPDATED, updated);

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberRespDTO lookupMember(String memberId, String ssnLast4) {
        Member member;

        if (memberId != null && ssnLast4 != null) {
            member = memberRepository.findByMemberIdAndSsnLast4(memberId, ssnLast4)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with memberId: " + memberId + " and SSN last 4"));
        } else if (memberId != null) {
            member = memberRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with memberId: " + memberId));
        } else if (ssnLast4 != null) {
            member = memberRepository.findBySsnLast4(ssnLast4)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Member not found with SSN last 4"));
        } else {
            throw new IllegalArgumentException("At least one of memberId or ssnLast4 must be provided");
        }

        return mapToResponse(member);
    }

    private void publishMemberEvent(MemberEventType eventType, Member member) {
        try {
            MemberEvent event = MemberEvent.builder()
                    .eventType(eventType)
                    .memberId(member.getId().toString())
                    .memberNumber(member.getMemberId())
                    .firstName(member.getFirstName())
                    .lastName(member.getLastName())
                    .timestamp(Instant.now())
                    .build();
            String topic = switch (eventType) {
                case MEMBER_CREATED -> "members-created";
                case MEMBER_UPDATED -> "members-updated";
                case MEMBER_DELETED -> "members-deleted";
            };
            eventPublisher.publish(topic, member.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {} event for member {}: {}", eventType, member.getMemberId(), e.getMessage());
        }
    }

    private Map<String, Object> memberToMap(Member member) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", member.getId() != null ? member.getId().toString() : null);
        map.put("memberId", member.getMemberId());
        map.put("firstName", member.getFirstName());
        map.put("lastName", member.getLastName());
        map.put("dateOfBirth", member.getDateOfBirth() != null ? member.getDateOfBirth().toString() : null);
        map.put("email", member.getEmail());
        map.put("phone", member.getPhone());
        map.put("address", member.getAddress());
        map.put("policyNumber", member.getPolicyNumber());
        map.put("policyStatus", member.getPolicyStatus());
        map.put("createdAt", member.getCreatedAt() != null ? member.getCreatedAt().toString() : null);
        map.put("updatedAt", member.getUpdatedAt() != null ? member.getUpdatedAt().toString() : null);
        return map;
    }

    private Member mapToEntity(MemberReqDTO request) {
        return Member.builder()
                .memberId(request.getMemberId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth() != null
                        ? LocalDate.parse(request.getDateOfBirth()) : null)
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .policyNumber(request.getPolicyNumber())
                .policyStatus(request.getPolicyStatus())
                .ssnLast4(request.getSsnLast4())
                .build();
    }

    private MemberRespDTO mapToResponse(Member member) {
        return MemberRespDTO.builder()
                .id(member.getId() != null ? member.getId().toString() : null)
                .memberId(member.getMemberId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth() != null
                        ? member.getDateOfBirth().toString() : null)
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .policyNumber(member.getPolicyNumber())
                .policyStatus(member.getPolicyStatus())
                .createdAt(member.getCreatedAt() != null
                        ? member.getCreatedAt().toString() : null)
                .updatedAt(member.getUpdatedAt() != null
                        ? member.getUpdatedAt().toString() : null)
                .build();
    }
}
