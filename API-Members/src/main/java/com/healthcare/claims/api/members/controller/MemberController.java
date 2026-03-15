package com.healthcare.claims.api.members.controller;

import com.healthcare.claims.common.members.dto.MemberReqDTO;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import com.healthcare.claims.api.members.service.MemberService;
import com.healthcare.claims.common.crypto.EncryptionService;
import com.healthcare.claims.common.crypto.IdType;
import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Member/customer CRUD, search, and lookup")
public class MemberController {

    private final MemberService memberService;
    private final EncryptionService encryptionService;

    @PostMapping
    @Operation(summary = "Create member")
    public ResponseEntity<ApiResponse<MemberRespDTO>> createMember(@Valid @RequestBody MemberReqDTO request) {
        MemberRespDTO response = memberService.createMember(request);
        encryptResponseIds(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get member by ID")
    public ResponseEntity<ApiResponse<MemberRespDTO>> getMemberById(@PathVariable String id) {
        String decryptedId = encryptionService.decrypt(id, IdType.MEMBER);
        MemberRespDTO response = memberService.getMemberById(UUID.fromString(decryptedId));
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Search members", description = "Search by firstName, lastName, memberId, dateOfBirth, policyNumber")
    public ResponseEntity<PagedResponse<MemberRespDTO>> searchMembers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth,
            @RequestParam(required = false) String policyNumber,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<MemberRespDTO> results = memberService.searchMembers(
                firstName, lastName, memberId, dateOfBirth, policyNumber, pageable);
        results.getContent().forEach(this::encryptResponseIds);
        return ResponseEntity.ok(PagedResponse.of(results));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing member")
    public ResponseEntity<ApiResponse<MemberRespDTO>> updateMember(
            @PathVariable String id,
            @Valid @RequestBody MemberReqDTO request) {
        String decryptedId = encryptionService.decrypt(id, IdType.MEMBER);
        MemberRespDTO response = memberService.updateMember(UUID.fromString(decryptedId), request);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lookup")
    @Operation(summary = "Lookup member", description = "Lookup by memberId and/or last 4 SSN digits")
    public ResponseEntity<ApiResponse<MemberRespDTO>> lookupMember(
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String ssnLast4) {
        MemberRespDTO response = memberService.lookupMember(memberId, ssnLast4);
        encryptResponseIds(response);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private void encryptResponseIds(MemberRespDTO response) {
        if (response.getId() != null) {
            response.setId(encryptionService.encrypt(response.getId(), IdType.MEMBER));
        }
        if (response.getTenantId() != null) {
            response.setTenantId(encryptionService.encrypt(response.getTenantId(), IdType.TENANT));
        }
    }
}
