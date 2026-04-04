package com.nexora.assessment.security;

import com.nexora.assessment.constants.ErrorMessages;
import com.nexora.assessment.constants.ServiceConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

    private final JwksKeyResolver jwksKeyResolver;

    public Claims extractClaims(String token) {
        RSAPublicKey rsaPublicKey = jwksKeyResolver.fetchPublicKey();
        return Jwts.parser()
                .verifyWith(rsaPublicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(Claims claims) {
        String userIdStr = claims.get(ServiceConstants.USER_ID_CLAIM, String.class);
        if (userIdStr == null) {
            throw new RuntimeException(ErrorMessages.USER_ID_NOT_FOUND_IN_TOKEN);
        }
        return UUID.fromString(userIdStr);
    }

    public List<String> extractRoles(Claims claims) {
        List<?> roles = claims.get(ServiceConstants.ROLES_CLAIM, List.class);
        return roles == null
                ? Collections.emptyList()
                : roles.stream().map(Object::toString).toList();
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
}
