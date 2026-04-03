package com.nexora.auth;

import com.nexora.auth.config.AppProperties;
import com.nexora.auth.config.MagicLinkProperties;
import com.nexora.auth.config.OtpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        OtpProperties.class,
        AppProperties.class,
        MagicLinkProperties.class
})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
