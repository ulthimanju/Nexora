package com.nexora.assessment.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nexora.jwt")
@Data
public class JwtConfig {
    /**
     * JWKS endpoint published by auth-service. Preferred for automatic key rotation.
     */
    private String jwksUri;

    /**
     * Optional fallback path to a local RSA public key (PEM) if JWKS is unavailable.
     */
    private String publicKeyPath;
}
