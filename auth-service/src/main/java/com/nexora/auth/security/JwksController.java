package com.nexora.auth.security;

import com.nexora.auth.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final JwtConfig jwtConfig;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        PublicKey publicKey = jwtConfig.getPublicKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        String n = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getModulus().toByteArray());
        String e = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(rsaPublicKey.getPublicExponent().toByteArray());

        return Map.of(
                "keys", new Object[]{
                        Map.of(
                                "kty", "RSA",
                                "use", "sig",
                                "alg", "RS256",
                                "n", n,
                                "e", e
                        )
                }
        );
    }
}
