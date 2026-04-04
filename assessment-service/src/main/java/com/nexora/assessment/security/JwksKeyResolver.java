package com.nexora.assessment.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

@Component
@Slf4j
public class JwksKeyResolver {

    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private volatile RSAPublicKey cachedKey;

    public JwksKeyResolver(JwtConfig jwtConfig, ObjectMapper objectMapper, RestTemplateBuilder restTemplateBuilder) {
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @PostConstruct
    public void init() {
        refreshKey();
    }

    @Scheduled(fixedDelayString = "PT24H")
    public void refreshKey() {
        try {
            RSAPublicKey key = loadPublicKey();
            if (key != null) {
                cachedKey = key;
                log.info("JWKS public key refreshed successfully");
            }
        } catch (Exception e) {
            log.error("Failed to refresh JWKS public key: {}", e.getMessage(), e);
        }
    }

    public RSAPublicKey fetchPublicKey() {
        if (cachedKey == null) {
            refreshKey();
        }
        return Optional.ofNullable(cachedKey)
                .orElseThrow(() -> new IllegalStateException("RSA public key not available for JWT verification"));
    }

    private RSAPublicKey loadPublicKey() throws Exception {
        if (StringUtils.hasText(jwtConfig.getJwksUri())) {
            RSAPublicKey key = loadFromJwks(jwtConfig.getJwksUri());
            if (key != null) {
                return key;
            }
        }

        if (StringUtils.hasText(jwtConfig.getPublicKeyPath())) {
            return loadFromPem(jwtConfig.getPublicKeyPath());
        }

        throw new IllegalStateException("No JWKS URI or public key path configured for JWT verification");
    }

    private RSAPublicKey loadFromJwks(String jwksUri) throws IOException {
        String response = restTemplate.getForObject(jwksUri, String.class);
        if (!StringUtils.hasText(response)) {
            throw new IllegalStateException("Empty response from JWKS endpoint");
        }

        JsonNode root = objectMapper.readTree(response);
        JsonNode keysNode = root.path("keys");

        if (!keysNode.isArray() || keysNode.isEmpty()) {
            throw new IllegalStateException("No keys found in JWKS response");
        }

        for (JsonNode keyNode : keysNode) {
            String kty = keyNode.path("kty").asText();
            String alg = keyNode.path("alg").asText();
            String use = keyNode.path("use").asText();
            if (!"RSA".equalsIgnoreCase(kty) || !"sig".equalsIgnoreCase(use)) {
                continue;
            }
            if (StringUtils.hasText(alg) && !"RS256".equalsIgnoreCase(alg)) {
                continue;
            }

            String modulus = keyNode.path("n").asText();
            String exponent = keyNode.path("e").asText();
            if (StringUtils.hasText(modulus) && StringUtils.hasText(exponent)) {
                return buildPublicKey(modulus, exponent);
            }
        }

        throw new IllegalStateException("No RSA signing key found in JWKS response");
    }

    private RSAPublicKey loadFromPem(String publicKeyPath) throws Exception {
        String pem = Files.readString(Path.of(publicKeyPath));
        String sanitized = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(sanitized);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec);
        return (RSAPublicKey) publicKey;
    }

    private RSAPublicKey buildPublicKey(String modulus, String exponent) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] eBytes = Base64.getUrlDecoder().decode(exponent);

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }
}
