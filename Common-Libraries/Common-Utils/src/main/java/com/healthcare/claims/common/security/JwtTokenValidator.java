package com.healthcare.claims.common.security;

import java.util.List;

public interface JwtTokenValidator {
    boolean isValid(String token);
    String getUsername(String token);
    String getTenantId(String token);
    List<String> getRoles(String token);
}
