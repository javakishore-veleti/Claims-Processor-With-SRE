package com.healthcare.claims.common.entitlements.client;

import com.healthcare.claims.common.dto.ApiResponse;
import com.healthcare.claims.common.entitlements.dto.LoginReqDTO;
import com.healthcare.claims.common.entitlements.dto.LoginRespDTO;
import com.healthcare.claims.common.entitlements.dto.SignupReqDTO;
import com.healthcare.claims.common.entitlements.dto.UserRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "api-auth", url = "${claims.service-client.services.api-entitlements.url:http://localhost:8086}")
public interface AuthFeignClient {

    @PostMapping("/api/v1/auth/login")
    ApiResponse<LoginRespDTO> login(@RequestBody LoginReqDTO request);

    @PostMapping("/api/v1/auth/logout")
    ApiResponse<Void> logout(@RequestHeader("Authorization") String token);

    @PostMapping("/api/v1/auth/signup")
    ApiResponse<UserRespDTO> signup(@RequestBody SignupReqDTO request);
}
