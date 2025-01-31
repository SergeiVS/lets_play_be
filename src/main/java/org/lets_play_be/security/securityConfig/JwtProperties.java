package org.lets_play_be.security.securityConfig;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
//@Configuration
//@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    @Value("${jwt.jwt-secret}")
    private String jwtSecret;
    @Value("${jwt.at-expiration-in-ms}")
    private Integer atExpirationInMs;
    @Value("{jwt.rt-expiration-in-ms}")
    private Integer rtExpirationInMs;
    @Value("${jwt.at-cookie-name}")
    private String atCookieName;
    @Value("${jwt.rt-cookie-name}")
    private String rtCookieName;
//    private Integer resetTokenExpirationInMs;
}

