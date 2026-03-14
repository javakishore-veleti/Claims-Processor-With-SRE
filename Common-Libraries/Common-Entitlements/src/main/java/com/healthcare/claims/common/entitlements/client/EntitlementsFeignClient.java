package com.healthcare.claims.common.entitlements.client;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.entitlements.dto.UserEntitlementRespDTO;
import com.healthcare.claims.common.entitlements.dto.UserReqDTO;
import com.healthcare.claims.common.entitlements.dto.UserRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "api-entitlements", url = "${claims.service-client.services.api-entitlements.url:http://localhost:8086}")
public interface EntitlementsFeignClient {

    @PostMapping("/api/v1/users")
    ApiResponse<UserRespDTO> createUser(@RequestBody UserReqDTO request);

    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserRespDTO> getUser(@PathVariable String id);

    @GetMapping("/api/v1/users")
    ApiResponse<List<UserRespDTO>> searchUsers(@RequestParam(required = false) String username,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size);

    @PutMapping("/api/v1/users/{id}")
    ApiResponse<UserRespDTO> updateUser(@PathVariable String id, @RequestBody UserReqDTO request);

    @GetMapping("/api/v1/users/{id}/entitlements")
    ApiResponse<UserEntitlementRespDTO> getUserEntitlements(@PathVariable String id);
}
