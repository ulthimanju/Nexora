package com.nexora.auth.service;

import com.nexora.auth.config.OtpProperties;
import com.nexora.auth.constants.LogMessages;
import com.nexora.auth.exception.AuthException;
import com.nexora.auth.exception.OtpException;
import com.nexora.auth.model.AccountStatus;
import com.nexora.auth.model.User;
import com.nexora.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

import static com.nexora.auth.constants.ErrorMessages.*;
import static com.nexora.auth.constants.ServiceConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final OtpProperties otpProperties;
    private final OtpEmailService otpEmailService;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public void sendRegistrationOtp(User user) {
        String normalizedEmail = normalizeEmail(user.getEmail());
        String otp = generateOtp();
        Duration ttl = registerTtl();
        cacheOtp(OTP_REGISTER_REDIS_PREFIX, normalizedEmail, otp, ttl);
        otpEmailService.sendOtpEmail(user.getEmail(), OTP_PURPOSE_REGISTER, otp, ttl);
        log.info(LogMessages.OTP_GENERATED, user.getEmail());
    }

    public User verifyRegistrationOtp(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);
        String cachedOtp = fetchOtp(OTP_REGISTER_REDIS_PREFIX, normalizedEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(String.format(USER_NOT_FOUND_WITH_EMAIL, email), HttpStatus.NOT_FOUND));
        if (user.getStatus() == AccountStatus.ACTIVE) {
            throw new AuthException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST);
        }
        if (cachedOtp == null) {
            throw new OtpException(OTP_EXPIRED, HttpStatus.GONE);
        }
        if (!cachedOtp.equals(otp)) {
            throw new OtpException(OTP_INVALID, HttpStatus.BAD_REQUEST);
        }
        redisTemplate.delete(OTP_REGISTER_REDIS_PREFIX + normalizedEmail);
        redisTemplate.delete(OTP_RESEND_REDIS_PREFIX + normalizedEmail);
        log.info(LogMessages.OTP_VERIFIED, email);
        return user;
    }

    public void resendRegistrationOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(String.format(USER_NOT_FOUND_WITH_EMAIL, email), HttpStatus.NOT_FOUND));
        if (user.getStatus() == AccountStatus.ACTIVE) {
            throw new AuthException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST);
        }
        long resendCount = incrementAndGetResendCount(normalizedEmail);
        if (resendCount > OTP_RESEND_LIMIT) {
            log.info(LogMessages.OTP_ATTEMPTS_EXCEEDED, email);
            throw new OtpException(OTP_RESEND_LIMIT_REACHED, HttpStatus.TOO_MANY_REQUESTS);
        }
        sendRegistrationOtp(user);
        log.info(LogMessages.OTP_RESEND_REQUEST, email);
    }

    public void requestLoginOtp(String email) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(String.format(USER_NOT_FOUND_WITH_EMAIL, email), HttpStatus.NOT_FOUND));
        ensureActive(user);
        String otp = generateOtp();
        Duration ttl = loginTtl();
        cacheOtp(OTP_LOGIN_REDIS_PREFIX, normalizedEmail, otp, ttl);
        redisTemplate.delete(OTP_ATTEMPTS_REDIS_PREFIX + normalizedEmail);
        otpEmailService.sendOtpEmail(email, OTP_PURPOSE_LOGIN, otp, ttl);
        log.info(LogMessages.LOGIN_OTP_REQUEST, email);
    }

    public User verifyLoginOtp(String email, String otp) {
        String normalizedEmail = normalizeEmail(email);
        enforceAttemptLimit(normalizedEmail);
        String cachedOtp = fetchOtp(OTP_LOGIN_REDIS_PREFIX, normalizedEmail);
        if (cachedOtp == null) {
            throw new OtpException(OTP_EXPIRED, HttpStatus.GONE);
        }
        if (!cachedOtp.equals(otp)) {
            long attempts = incrementAttempt(normalizedEmail, loginTtl());
            if (attempts >= maxAttempts()) {
                log.info(LogMessages.OTP_ATTEMPTS_EXCEEDED, email);
                throw new OtpException(OTP_ATTEMPTS_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);
            }
            throw new OtpException(OTP_INVALID, HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(String.format(USER_NOT_FOUND_WITH_EMAIL, email), HttpStatus.NOT_FOUND));
        ensureActive(user);
        redisTemplate.delete(OTP_LOGIN_REDIS_PREFIX + normalizedEmail);
        redisTemplate.delete(OTP_ATTEMPTS_REDIS_PREFIX + normalizedEmail);
        log.info(LogMessages.OTP_VERIFIED, email);
        return user;
    }

    private void enforceAttemptLimit(String normalizedEmail) {
        String attemptsKey = OTP_ATTEMPTS_REDIS_PREFIX + normalizedEmail;
        String currentAttempts = redisTemplate.opsForValue().get(attemptsKey);
        if (currentAttempts != null) {
            int attempts = Integer.parseInt(currentAttempts);
            if (attempts >= maxAttempts()) {
                throw new OtpException(OTP_ATTEMPTS_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);
            }
        }
    }

    private long incrementAttempt(String normalizedEmail, Duration ttl) {
        String attemptsKey = OTP_ATTEMPTS_REDIS_PREFIX + normalizedEmail;
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, ttl);
        }
        return attempts == null ? 0 : attempts;
    }

    private long incrementAndGetResendCount(String normalizedEmail) {
        String key = OTP_RESEND_REDIS_PREFIX + normalizedEmail;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(OTP_RESEND_WINDOW_MINUTES));
        }
        return count == null ? 0 : count;
    }

    private void cacheOtp(String prefix, String email, String otp, Duration ttl) {
        redisTemplate.opsForValue().set(prefix + email, otp, ttl);
    }

    private String fetchOtp(String prefix, String email) {
        return redisTemplate.opsForValue().get(prefix + email);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int value = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", value);
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    private int maxAttempts() {
        return otpProperties.getMaxAttempts() > 0 ? otpProperties.getMaxAttempts() : OTP_RESEND_LIMIT;
    }

    private Duration registerTtl() {
        return otpProperties.getTtlRegister() != null ? otpProperties.getTtlRegister() : Duration.ofMinutes(10);
    }

    private Duration loginTtl() {
        return otpProperties.getTtlLogin() != null ? otpProperties.getTtlLogin() : Duration.ofMinutes(5);
    }

    private void ensureActive(User user) {
        if (user.getStatus() == null || user.getStatus() == AccountStatus.PENDING) {
            throw new AuthException(ACCOUNT_PENDING_VERIFICATION, HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() == AccountStatus.SUSPENDED) {
            throw new AuthException(ACCOUNT_SUSPENDED, HttpStatus.FORBIDDEN);
        }
    }
}
