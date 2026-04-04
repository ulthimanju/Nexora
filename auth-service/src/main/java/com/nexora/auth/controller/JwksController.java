package com.nexora.auth.controller;

import com.nexora.auth.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static com.nexora.auth.constants.ServiceConstants.JWKS_ALGORITHM;
import static com.nexora.auth.constants.ServiceConstants.RSA_ALGORITHM;
import static com.nexora.auth.constants.ServiceConstants.JWKS_ALGORITHM_RS256;
import static com.nexora.auth.constants.ServiceConstants.JWKS_KEY_TYPE;
import static com.nexora.auth.constants.ServiceConstants.JWKS_KEYS_KEY;
import static com.nexora.auth.constants.ServiceConstants.JWKS_MODULUS;
import static com.nexora.auth.constants.ServiceConstants.JWKS_PATH;
import static com.nexora.auth.constants.ServiceConstants.JWKS_USE;
import static com.nexora.auth.constants.ServiceConstants.JWKS_USE_SIGNATURE;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtConfig jwtConfig;

    @GetMapping(JWKS_PATH)
    public ResponseEntity<Map<String, Object>> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) jwtConfig.getPublicKey();

        Map<String, String> jwk = Map.of(
                JWKS_KEY_TYPE, RSA_ALGORITHM,
                JWKS_USE, JWKS_USE_SIGNATURE,
                JWKS_ALGORITHM, JWKS_ALGORITHM_RS256,
                JWKS_MODULUS, encodeUrl(publicKey.getModulus().toByteArray()),
                JWKS_EXPONENT, encodeUrl(publicKey.getPublicExponent().toByteArray())
        );

        return ResponseEntity.ok(Map.of(JWKS_KEYS_KEY, List.of(jwk)));
    }

    private String encodeUrl(byte[] value) {
        byte[] sanitized = value.length > 1 && value[0] == 0
                ? java.util.Arrays.copyOfRange(value, 1, value.length)
                : value;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sanitized);
    }
}
