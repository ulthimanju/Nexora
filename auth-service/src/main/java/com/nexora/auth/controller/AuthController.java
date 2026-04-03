// Refactored: extracted 7 constants
package com.nexora.auth.controller;

import com.nexora.auth.dto.request.LoginRequest;
import com.nexora.auth.dto.request.RegisterRequest;
import com.nexora.auth.dto.response.AuthResponse;
import com.nexora.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.nexora.auth.constants.ServiceConstants.*;

@RestController
@RequestMapping(AUTH_BASE_PATH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(AUTH_REGISTER_PATH)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_REFRESH_PATH)
    public ResponseEntity<AuthResponse> refresh(@RequestHeader(AUTHORIZATION_HEADER) String authHeader) {
        String refreshToken = authHeader.substring(BEARER_PREFIX.length());
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_LOGOUT_PATH)
    public ResponseEntity<Void> logout(@RequestHeader(AUTHORIZATION_HEADER) String authHeader) {
        String refreshToken = authHeader.substring(BEARER_PREFIX.length());
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
