package com.nexora.auth.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@Slf4j
@Getter
public class JwtConfig {

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.private-key:}")
    private String privateKeyStr;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private KeyPair keyPair;
    private SecretKey secretKey;

    public JwtConfig() {
        try {
            // Generate RSA keypair for RS256
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            this.keyPair = keyGen.generateKeyPair();
            log.info("Generated RSA keypair for JWT signing");
        } catch (Exception e) {
            log.error("Failed to generate RSA keypair", e);
            throw new RuntimeException("Failed to initialize JWT configuration", e);
        }
    }

    public PrivateKey getPrivateKey() {
        if (privateKeyStr != null && !privateKeyStr.isEmpty()) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance("RSA");
                return kf.generatePrivate(spec);
            } catch (Exception e) {
                log.warn("Failed to load private key from config, using generated key", e);
            }
        }
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public SecretKey getSecretKey() {
        if (secretKey == null && secret != null && !secret.isEmpty()) {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            secretKey = Keys.hmacShaKeyFor(keyBytes);
        }
        return secretKey;
    }
}
