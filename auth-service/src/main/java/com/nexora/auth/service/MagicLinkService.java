package com.nexora.auth.service;

import com.nexora.auth.config.AppProperties;
import com.nexora.auth.config.MagicLinkProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

import static com.nexora.auth.constants.ServiceConstants.MAGIC_LINK_RATE_REDIS_PREFIX;
import static com.nexora.auth.constants.ServiceConstants.MAGIC_LINK_REDIS_PREFIX;

@Service
public class MagicLinkService {

    private final StringRedisTemplate redisTemplate;
    private final MagicLinkProperties magicLinkProperties;
    private final AppProperties appProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public MagicLinkService(StringRedisTemplate redisTemplate, MagicLinkProperties magicLinkProperties, AppProperties appProperties) {
        this.redisTemplate = redisTemplate;
        this.magicLinkProperties = magicLinkProperties;
        this.appProperties = appProperties;
    }

    public String generateToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public void saveMagicLink(String email, String token) {
        String key = MAGIC_LINK_REDIS_PREFIX + token;
        redisTemplate.opsForValue().set(key, email, tokenTtl());
    }

    public Optional<String> getEmailByToken(String token) {
        String key = MAGIC_LINK_REDIS_PREFIX + token;
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void consumeToken(String token) {
        redisTemplate.delete(MAGIC_LINK_REDIS_PREFIX + token);
    }

    public boolean checkRateLimit(String email) {
        String key = MAGIC_LINK_RATE_REDIS_PREFIX + email;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, tokenTtl());
        }
        int maxRequests = magicLinkProperties.getMaxRequests() > 0 ? magicLinkProperties.getMaxRequests() : 3;
        return count != null && count <= maxRequests;
    }

    public String buildMagicLinkUrl(String token) {
        String baseUrl = appProperties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:3000";
        }
        String normalizedBase = baseUrl != null && baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalizedBase + "/auth/magic-link/verify?token=" + token;
    }

    private Duration tokenTtl() {
        long ttlSeconds = magicLinkProperties.getTtl() > 0 ? magicLinkProperties.getTtl() : 900L;
        return Duration.ofSeconds(ttlSeconds);
    }
}
