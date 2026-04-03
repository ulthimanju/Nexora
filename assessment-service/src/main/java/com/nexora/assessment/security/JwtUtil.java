package com.nexora.assessment.security;

import com.nexora.assessment.constants.ErrorMessages;
import com.nexora.assessment.constants.ServiceConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final JwtConfig jwtConfig;

    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        String userIdStr = claims.get(ServiceConstants.USER_ID_CLAIM, String.class);
        if (userIdStr == null) {
            throw new RuntimeException(ErrorMessages.USER_ID_NOT_FOUND_IN_TOKEN);
        }
        return UUID.fromString(userIdStr);
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
