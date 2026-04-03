// Refactored: extracted 10 constants
package com.nexora.auth.security;

import com.nexora.auth.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;
import static com.nexora.auth.constants.ServiceConstants.*;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtConfig jwtConfig;

    @GetMapping(JWKS_PATH)
    public Map<String, Object> getJwks() {
        PublicKey publicKey = jwtConfig.getPublicKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getModulus().toByteArray());
        String e = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

        return Map.of(
                JWKS_KEYS_KEY, new Object[]{
                        Map.of(
                                JWKS_KEY_TYPE, RSA_ALGORITHM,
                                JWKS_USE, JWKS_USE_SIGNATURE,
                                JWKS_ALGORITHM, JWKS_ALGORITHM_RS256,
                                JWKS_MODULUS, n,
                                JWKS_EXPONENT, e
                        )
                }
        );
    }
}
