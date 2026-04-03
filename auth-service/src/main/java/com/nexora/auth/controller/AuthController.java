// Refactored: extracted 7 constants
package com.nexora.auth.controller;

import com.nexora.auth.dto.request.LoginOtpRequest;
import com.nexora.auth.dto.request.LoginRequest;
import com.nexora.auth.dto.request.OtpVerificationRequest;
import com.nexora.auth.dto.request.RegisterRequest;
import com.nexora.auth.dto.response.AuthResponse;
import com.nexora.auth.dto.response.MessageResponse;
import com.nexora.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.nexora.auth.constants.ServiceConstants.*;
import static com.nexora.auth.constants.ValidationMessages.EMAIL_INVALID;
import static com.nexora.auth.constants.ValidationMessages.EMAIL_REQUIRED;

@RestController
@RequestMapping(AUTH_BASE_PATH)
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping(AUTH_REGISTER_PATH)
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(AUTH_VERIFY_EMAIL_PATH)
    public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody OtpVerificationRequest request) {
        AuthResponse response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_RESEND_OTP_PATH)
    public ResponseEntity<MessageResponse> resendOtp(
            @RequestParam("type") String type,
            @RequestParam("email") @NotBlank(message = EMAIL_REQUIRED) @Email(message = EMAIL_INVALID) String email
    ) {
        MessageResponse response = authService.resendOtp(type, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_LOGIN_PATH)
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_LOGIN_OTP_REQUEST_PATH)
    public ResponseEntity<MessageResponse> requestLoginOtp(@Valid @RequestBody LoginOtpRequest request) {
        MessageResponse response = authService.requestLoginOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(AUTH_LOGIN_OTP_VERIFY_PATH)
    public ResponseEntity<AuthResponse> verifyLoginOtp(@Valid @RequestBody OtpVerificationRequest request) {
        AuthResponse response = authService.verifyLoginOtp(request);
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
