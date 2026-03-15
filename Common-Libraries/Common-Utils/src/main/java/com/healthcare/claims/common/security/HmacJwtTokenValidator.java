package com.healthcare.claims.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class HmacJwtTokenValidator implements JwtTokenValidator {
    private final SecretKey key;

    public HmacJwtTokenValidator(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getUsername(String token) { return parse(token).getSubject(); }

    @Override
    public String getTenantId(String token) { return parse(token).get("tenantId", String.class); }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) { return parse(token).get("roles", List.class); }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
