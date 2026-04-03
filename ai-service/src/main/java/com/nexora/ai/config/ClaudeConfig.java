package com.nexora.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.claude")
@Getter
@Setter
public class ClaudeConfig {
    private String apiKey;
    private String model;
    private String baseUrl;
    private int maxTokens;
}
