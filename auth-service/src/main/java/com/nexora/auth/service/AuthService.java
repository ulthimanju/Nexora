package com.nexora.auth.service;

import com.nexora.auth.dto.request.LoginRequest;
import com.nexora.auth.dto.request.RegisterRequest;
import com.nexora.auth.dto.response.AuthResponse;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseThrow(() -> new AuthException("Default role not found"));
        user.getRoles().add(studentRole);

        user = userRepository.save(user);
        log.info("User registered: {}", user.getUsername());

        return generateAuthResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            log.info("User logged in: {}", user.getUsername());

            return generateAuthResponse(user);
        } catch (BadCredentialsException e) {
            log.error("Failed login attempt for: {}", request.getUsernameOrEmail());
            throw new AuthException("Invalid username or password");
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        try {
            String username = tokenService.extractUsername(refreshTokenStr);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String tokenHash = hashToken(refreshTokenStr);
            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(() -> new AuthException("Refresh token not found"));

            if (refreshToken.isRevoked()) {
                throw new AuthException("Refresh token has been revoked");
            }

            if (refreshToken.isExpired()) {
                throw new AuthException("Refresh token has expired");
            }

            String redisKey = "refresh_token:" + tokenHash;
            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                throw new AuthException("Refresh token not found in cache");
            }

            return generateAuthResponse(user);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new AuthException("Invalid refresh token");
        }
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        try {
            String tokenHash = hashToken(refreshTokenStr);
            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                    .orElseThrow(() -> new AuthException("Refresh token not found"));

            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            String redisKey = "refresh_token:" + tokenHash;
            redisTemplate.delete(redisKey);

            log.info("User logged out");
        } catch (Exception e) {
            log.error("Logout failed", e);
            throw new AuthException("Logout failed");
        }
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = tokenService.generateAccessToken(user);
        String refreshTokenStr = tokenService.generateRefreshToken(user);
        String tokenHash = hashToken(refreshTokenStr);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        String redisKey = "refresh_token:" + tokenHash;
        redisTemplate.opsForValue().set(redisKey, user.getUsername(), 7, TimeUnit.DAYS);

        Set<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(900L)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new AuthException("Failed to hash token", e);
        }
    }
}
