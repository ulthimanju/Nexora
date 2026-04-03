package com.nexora.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "nexora.magic-link")
public class MagicLinkProperties {
    private long ttl;
    private int maxRequests;
}
