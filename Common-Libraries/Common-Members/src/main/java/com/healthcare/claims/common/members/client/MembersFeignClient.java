package com.healthcare.claims.common.members.client;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.members.dto.MemberReqDTO;
import com.healthcare.claims.common.members.dto.MemberRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "api-members", url = "${claims.service-client.services.api-members.url:http://localhost:8084}")
public interface MembersFeignClient {

    @PostMapping("/api/v1/members")
    ApiResponse<MemberRespDTO> createMember(@RequestBody MemberReqDTO request);

    @GetMapping("/api/v1/members/{id}")
    ApiResponse<MemberRespDTO> getMember(@PathVariable String id);

    @GetMapping("/api/v1/members")
    ApiResponse<List<MemberRespDTO>> searchMembers(@RequestParam(required = false) String firstName,
                                                    @RequestParam(required = false) String lastName,
                                                    @RequestParam(required = false) String memberId);

    @GetMapping("/api/v1/members/lookup")
    ApiResponse<MemberRespDTO> lookupMember(@RequestParam(required = false) String memberId,
                                             @RequestParam(required = false) String ssnLast4);

    @PutMapping("/api/v1/members/{id}")
    ApiResponse<MemberRespDTO> updateMember(@PathVariable String id, @RequestBody MemberReqDTO request);
}
