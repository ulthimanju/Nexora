package com.nexora.ai.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.ai.dto.request.GenerateAssessmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ai.cache.question-ttl-hours}")
    private int ttlHours;

    public String getCachedQuestions(GenerateAssessmentRequest request) {
        String key = buildCacheKey(request);
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.info("Cache hit for key: {}", key);
                return cached;
            }
            log.info("Cache miss for key: {}", key);
            return null;
        } catch (Exception e) {
            log.error("Error retrieving from cache", e);
            return null;
        }
    }

    public void cacheQuestions(GenerateAssessmentRequest request, String jsonResponse) {
        String key = buildCacheKey(request);
        try {
            redisTemplate.opsForValue().set(key, jsonResponse, ttlHours, TimeUnit.HOURS);
            log.info("Cached questions with key: {} for {} hours", key, ttlHours);
        } catch (Exception e) {
            log.error("Error caching questions", e);
        }
    }

    private String buildCacheKey(GenerateAssessmentRequest request) {
        return String.format("ai:assessment:%s:%d:%d:%s",
                request.getTopic().toLowerCase().replace(" ", "-"),
                request.getMcqCount(),
                request.getCodingCount(),
                request.getDifficultyHint() != null ? request.getDifficultyHint() : "MIXED"
        );
    }
}
