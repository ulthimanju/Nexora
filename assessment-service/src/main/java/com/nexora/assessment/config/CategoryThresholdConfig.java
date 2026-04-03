package com.nexora.assessment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "nexora.category.thresholds")
@Getter
@Setter
public class CategoryThresholdConfig {

    private Integer beginner = 0;
    private Integer basic = 8;
    private Integer advanced = 14;
    private Integer expert = 18;
}
