// Refactored: extracted 1 constant
package com.nexora.auth.service;

import com.nexora.auth.config.JwtConfig;
import com.nexora.auth.model.TokenType;
import com.nexora.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nexora.auth.constants.ServiceConstants.ROLES_CLAIM_KEY;
import static com.nexora.auth.constants.ServiceConstants.TOKEN_TYPE_CLAIM_KEY;
import static com.nexora.auth.constants.ServiceConstants.USER_ID_CLAIM_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put(USER_ID_CLAIM_KEY, user.getId().toString());
        }
        claims.put(ROLES_CLAIM_KEY, userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put(TOKEN_TYPE_CLAIM_KEY, TokenType.ACCESS.name());
        return createToken(claims, userDetails.getUsername(), jwtConfig.getExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TOKEN_TYPE_CLAIM_KEY, TokenType.REFRESH.name());
        return createToken(claims, userDetails.getUsername(), jwtConfig.getRefreshExpiration());
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtConfig.getPrivateKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public TokenType extractTokenType(String token) {
        String tokenType = extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM_KEY, String.class));
        if (tokenType == null) {
            return null;
        }
        try {
            return TokenType.valueOf(tokenType);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown token type: {}", tokenType);
            return null;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, TokenType.ACCESS);
    }

    public Boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, TokenType.REFRESH);
    }

    private Boolean isTokenValid(String token, UserDetails userDetails, TokenType expectedType) {
        TokenType tokenType = extractTokenType(token);
        if (tokenType != expectedType) {
            return false;
        }
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
