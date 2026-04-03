package com.nexora.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "nexora.otp")
public class OtpProperties {
    private Duration ttlRegister;
    private Duration ttlLogin;
    private int maxAttempts;
}
