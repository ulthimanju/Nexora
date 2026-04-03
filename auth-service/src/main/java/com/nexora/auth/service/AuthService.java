package com.nexora.auth.service;

import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.dto.request.LoginOtpRequest;
import com.nexora.auth.dto.request.LoginRequest;
import com.nexora.auth.dto.request.OtpVerificationRequest;
import com.nexora.auth.dto.request.RegisterRequest;
import com.nexora.auth.dto.response.AuthResponse;
import com.nexora.auth.dto.response.MessageResponse;
import com.nexora.auth.exception.AuthException;
import com.nexora.auth.model.RefreshToken;
import com.nexora.auth.model.Role;
import com.nexora.auth.model.User;
import com.nexora.auth.repository.RefreshTokenRepository;
import com.nexora.auth.repository.RoleRepository;
import com.nexora.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nexora.auth.constants.ErrorMessages.*;
import static com.nexora.auth.constants.ServiceConstants.*;
import static com.nexora.auth.model.AccountStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final Set<String> SELF_SERVICE_ROLES = Set.of(ROLE_STUDENT, ROLE_INSTRUCTOR, ROLE_GUEST);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final OtpService otpService;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException(USERNAME_EXISTS, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(PENDING);

        String roleName = resolveRoleName(request.getRole());
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AuthException(String.format(ROLE_NOT_FOUND_WITH_NAME, roleName), HttpStatus.BAD_REQUEST));
        user.getRoles().add(role);

        user = userRepository.save(user);
        log.info(LogMessages.USER_REGISTERED, user.getUsername());

        otpService.sendRegistrationOtp(user);
        return new MessageResponse(REGISTER_INITIATED_MESSAGE);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            User user = loadUserByUsernameOrEmail(request.getUsernameOrEmail());
            validateActiveAccount(user);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            request.getPassword()
                    )
            );

            User authenticatedUser = (User) authentication.getPrincipal();
            log.info(LogMessages.USER_LOGGED_IN, authenticatedUser.getUsername());

            return generateAuthResponse(authenticatedUser);
        } catch (BadCredentialsException e) {
            log.error(LogMessages.FAILED_LOGIN_ATTEMPT, request.getUsernameOrEmail());
            throw new AuthException(INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        try {
            String username = tokenService.extractUsername(refreshTokenStr);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

            validateActiveAccount(user);

            String tokenHash = hashToken(refreshTokenStr);
            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(() -> new AuthException(REFRESH_TOKEN_NOT_FOUND, HttpStatus.UNAUTHORIZED));

            if (refreshToken.isRevoked()) {
                throw new AuthException(REFRESH_TOKEN_REVOKED, HttpStatus.UNAUTHORIZED);
            }

            if (refreshToken.isExpired()) {
                throw new AuthException(REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
            }

            String redisKey = REFRESH_TOKEN_REDIS_PREFIX + tokenHash;
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                throw new AuthException(REFRESH_TOKEN_CACHE_MISS, HttpStatus.UNAUTHORIZED);
            }

            return generateAuthResponse(user);
        } catch (Exception e) {
            log.error(LogMessages.TOKEN_REFRESH_FAILED, e);
            throw new AuthException(INVALID_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        try {
            String tokenHash = hashToken(refreshTokenStr);
            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(() -> new AuthException(REFRESH_TOKEN_NOT_FOUND, HttpStatus.UNAUTHORIZED));

            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            String redisKey = REFRESH_TOKEN_REDIS_PREFIX + tokenHash;
            redisTemplate.delete(redisKey);

            log.info(LogMessages.USER_LOGGED_OUT);
        } catch (Exception e) {
            log.error(LogMessages.LOGOUT_FAILED, e);
            throw new AuthException(LOGOUT_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public AuthResponse verifyEmail(OtpVerificationRequest request) {
        User user = otpService.verifyRegistrationOtp(request.getEmail(), request.getOtp());
        user.setStatus(ACTIVE);
        userRepository.save(user);
        log.info(LogMessages.ACCOUNT_ACTIVATED, user.getEmail());
        return generateAuthResponse(user);
    }

    @Transactional
    public MessageResponse resendOtp(String type, String email) {
        if (!OTP_TYPE_REGISTER.equalsIgnoreCase(type)) {
            throw new AuthException(OTP_TYPE_NOT_SUPPORTED, HttpStatus.BAD_REQUEST);
        }
        otpService.resendRegistrationOtp(email);
        return new MessageResponse(REGISTER_OTP_SENT_MESSAGE);
    }

    @Transactional
    public MessageResponse requestLoginOtp(LoginOtpRequest request) {
        otpService.requestLoginOtp(request.getEmail());
        return new MessageResponse(LOGIN_OTP_SENT_MESSAGE);
    }

    @Transactional
    public AuthResponse verifyLoginOtp(OtpVerificationRequest request) {
        User user = otpService.verifyLoginOtp(request.getEmail(), request.getOtp());
        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse generateAuthResponse(User user) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshTokenStr = tokenService.generateRefreshToken(user);
        String tokenHash = hashToken(refreshTokenStr);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        String redisKey = REFRESH_TOKEN_REDIS_PREFIX + tokenHash;
        redisTemplate.opsForValue().set(redisKey, user.getUsername(), 7, TimeUnit.DAYS);

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType(BEARER_TOKEN_TYPE)
                .expiresIn(900L)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    private void validateActiveAccount(User user) {
        if (user.getStatus() == null || user.getStatus() == PENDING) {
            throw new AuthException(ACCOUNT_PENDING_VERIFICATION, HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() == SUSPENDED) {
            throw new AuthException(ACCOUNT_SUSPENDED, HttpStatus.FORBIDDEN);
        }
    }

    private String resolveRoleName(String role) {
        String normalized = role.toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        if (!SELF_SERVICE_ROLES.contains(normalized)) {
            throw new AuthException(String.format(ROLE_NOT_FOUND_WITH_NAME, normalized), HttpStatus.BAD_REQUEST);
        }
        return normalized;
    }

    private User loadUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_WITH_USERNAME, usernameOrEmail)
                ));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256_ALGORITHM);
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new AuthException(HASH_TOKEN_FAILED, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
