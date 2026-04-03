package com.nexora.auth.controller;

import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.dto.request.MagicLinkRequest;
import com.nexora.auth.dto.response.AuthResponse;
import com.nexora.auth.dto.response.MessageResponse;
import com.nexora.auth.exception.AuthException;
import com.nexora.auth.exception.MagicLinkExpiredException;
import com.nexora.auth.exception.MagicLinkRateLimitException;
import com.nexora.auth.model.AccountStatus;
import com.nexora.auth.model.User;
import com.nexora.auth.repository.UserRepository;
import com.nexora.auth.service.AuthService;
import com.nexora.auth.service.MagicLinkEmailService;
import com.nexora.auth.service.MagicLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static com.nexora.auth.constants.ErrorMessages.*;
import static com.nexora.auth.constants.LogMessages.MAGIC_LINK_VERIFIED;
import static com.nexora.auth.constants.ServiceConstants.*;

@RestController
@RequestMapping(AUTH_BASE_PATH + AUTH_MAGIC_LINK_BASE_PATH)
@RequiredArgsConstructor
@Slf4j
public class MagicLinkController {

    private final MagicLinkService magicLinkService;
    private final MagicLinkEmailService magicLinkEmailService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping(AUTH_MAGIC_LINK_REQUEST_PATH)
    public ResponseEntity<MessageResponse> requestMagicLink(@Valid @RequestBody MagicLinkRequest request) {
        String email = request.getEmail();
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(MAGIC_LINK_EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            log.info(LogMessages.MAGIC_LINK_ACCOUNT_SUSPENDED, normalizedEmail);
            throw new AuthException(MAGIC_LINK_ACCOUNT_SUSPENDED, HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AuthException(ACCOUNT_PENDING_VERIFICATION, HttpStatus.FORBIDDEN);
        }

        boolean allowed = magicLinkService.checkRateLimit(normalizedEmail);
        if (!allowed) {
            log.info(LogMessages.MAGIC_LINK_RATE_LIMITED, normalizedEmail);
            throw new MagicLinkRateLimitException(MAGIC_LINK_RATE_LIMIT_EXCEEDED);
        }

        log.info(LogMessages.MAGIC_LINK_REQUESTED, normalizedEmail);
        String token = magicLinkService.generateToken();
        magicLinkService.saveMagicLink(user.getEmail(), token);
        String magicLinkUrl = magicLinkService.buildMagicLinkUrl(token);
        magicLinkEmailService.sendMagicLinkEmail(user.getEmail(), magicLinkUrl);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new MessageResponse(MAGIC_LINK_SENT_MESSAGE));
    }

    @GetMapping(AUTH_MAGIC_LINK_VERIFY_PATH)
    public ResponseEntity<AuthResponse> verifyMagicLink(@RequestParam("token") String token) {
        String email = magicLinkService.getEmailByToken(token)
                .orElseThrow(() -> {
                    log.info(LogMessages.MAGIC_LINK_EXPIRED_ATTEMPT, token);
                    return new MagicLinkExpiredException(MAGIC_LINK_EXPIRED);
                });

        magicLinkService.consumeToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(MAGIC_LINK_EMAIL_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (user.getStatus() == AccountStatus.SUSPENDED) {
            log.info(LogMessages.MAGIC_LINK_ACCOUNT_SUSPENDED, email);
            throw new AuthException(MAGIC_LINK_ACCOUNT_SUSPENDED, HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new AuthException(ACCOUNT_PENDING_VERIFICATION, HttpStatus.FORBIDDEN);
        }

        log.info(MAGIC_LINK_VERIFIED, email);
        AuthResponse response = authService.generateAuthResponse(user);
        return ResponseEntity.ok(response);
    }
}
