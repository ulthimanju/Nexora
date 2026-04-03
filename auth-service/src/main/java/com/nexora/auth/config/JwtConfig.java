// Refactored: extracted 5 constants
package com.nexora.auth.config;

import com.nexora.auth.constants.LogMessages;
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
import static com.nexora.auth.constants.ErrorMessages.FAILED_INIT_JWT_CONFIG;
import static com.nexora.auth.constants.ServiceConstants.RSA_ALGORITHM;

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
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyGen.initialize(2048);
            this.keyPair = keyGen.generateKeyPair();
            log.info(LogMessages.GENERATED_KEYPAIR);
        } catch (Exception e) {
            log.error(LogMessages.FAILED_GENERATE_KEYPAIR, e);
            throw new RuntimeException(FAILED_INIT_JWT_CONFIG, e);
        }
    }

    public PrivateKey getPrivateKey() {
        if (privateKeyStr != null && !privateKeyStr.isEmpty()) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
                return kf.generatePrivate(spec);
            } catch (Exception e) {
                log.warn(LogMessages.LOAD_PRIVATE_KEY_FALLBACK, e);
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
