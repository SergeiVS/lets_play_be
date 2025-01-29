package org.lets_play_be.security.securityConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class jwtProperties {
    private String jwtSecret;
    private Integer atExpirationInMs;
    private Integer rtExpirationInMs;
    private String atCookieName;
    private String rtCookieName;
    private Integer resetTokenExpirationInMs;
}

