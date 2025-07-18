package org.lets_play_be.security.utils;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.BlacklistedToken;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.repository.BlacklistedTokenRepository;
import org.lets_play_be.security.securityConfig.JwtProperties;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtProperties properties;
    @Mock
    private BlacklistedTokenRepository tokenRepository;

    @InjectMocks
    private JwtService jwtService;

    private Date currentdate;
    private String email;
    private AppUserRole role;
    private String accessToken;
    private String refreshToken;
    private AppUser user;
    private UserDetails userDetails;
    private MockHttpServletRequest request;
    private Cookie atCookie;
    private Cookie rtCookie;

    @BeforeEach
    void setUp() {

        when(properties.getJwtSecret()).thenReturn("GWS3eDKYYoaZISBxbUINjvhreiiYHSAg");
        when(properties.getAtExpirationInMs()).thenReturn(900000);
        when(properties.getRtExpirationInMs()).thenReturn(604800000);
        when(properties.getAtCookieName()).thenReturn("access-token");
        when(properties.getRtCookieName()).thenReturn("refresh-token");

        currentdate = new Date(System.currentTimeMillis());
        email = "test@test.com";
        role = new AppUserRole(1L, "ROLE_USER");
        Date atExpiration = new Date(currentdate.getTime() + properties.getAtExpirationInMs());
        Date rtExpiration = new Date(currentdate.getTime() + properties.getRtExpirationInMs());
        accessToken = generateJwtToken(email, atExpiration, List.of(role));
        refreshToken = generateJwtToken(email, rtExpiration, List.of(role));
        user = new AppUser("", email, "", "");
        user.getRoles().add(role);
        userDetails = new UserDetailsMapper(user);
        atCookie = new Cookie(properties.getAtCookieName(), accessToken);
        rtCookie = new Cookie(properties.getRtCookieName(), refreshToken);
        request = new MockHttpServletRequest();
    }

    @AfterEach
    void tearDown() {
        currentdate = null;
        email = null;
        role = null;
        accessToken = null;
        refreshToken = null;
        user = null;
        userDetails = null;
        atCookie = null;
        rtCookie = null;
        request = null;
    }

    @Test
    void getUsernameFromToken() {
        var result = jwtService.getUsernameFromToken(accessToken);

        assertThat(result).isEqualTo(email);
    }

    @Test
    void isTokenExpired_ReturnsFalse() {
        var result = jwtService.isTokenExpired(accessToken);

        assertThat(result).isFalse();
    }

    @Test
    void isTokenExpired_ReturnsTrue() {

        var expirationDateBefore = new Date(System.currentTimeMillis() - properties.getRtExpirationInMs());
        var token = generateJwtToken(email, expirationDateBefore, List.of(role));

        assertThrows(JwtException.class, () -> jwtService.isTokenExpired(token), "Token is expired");
    }

    @Test
    void validateToken_Success() {

        assertTrue(jwtService.validateToken(accessToken, userDetails));
    }

    @Test
    void validateToken_TokenNotValid() {

        var falseMail = "email@email.com";
        var expiration = new Date(currentdate.getTime() + properties.getAtExpirationInMs());
        var falseToken = generateJwtToken(falseMail, expiration, List.of(role));

        assertFalse(jwtService.validateToken(falseToken, userDetails));
    }

    @Test
    void validateToken_Throws_TokenIsExpired() {

        var expiration = new Date(currentdate.getTime() - properties.getAtExpirationInMs());
        var falseToken = generateJwtToken(email, expiration, List.of(role));

        assertThrows(JwtException.class, () -> jwtService.validateToken(falseToken, userDetails), "Token is expired");
    }

    @Test
    void isRefreshTokenBlacklisted_ReturnsFalse() {
        when(tokenRepository.getByRefreshToken(refreshToken)).thenReturn(Optional.empty());

        assertFalse(jwtService.isRefreshTokenBlacklisted(refreshToken));

        verify(tokenRepository, times(1)).getByRefreshToken(refreshToken);
    }

    @Test
    void isRefreshTokenBlacklisted_ReturnsTrue() {

        BlacklistedToken blacklistedToken = new BlacklistedToken(user, refreshToken, OffsetDateTime.now());

        when(tokenRepository.getByRefreshToken(refreshToken)).thenReturn(Optional.of(blacklistedToken));

        assertTrue(jwtService.isRefreshTokenBlacklisted(refreshToken));

        verify(tokenRepository, times(1)).getByRefreshToken(refreshToken);
    }

    @Test
    void getRefreshTokenFromCookie_Success() {

        request.setCookies(atCookie, rtCookie);

        var result = jwtService.getRefreshTokenFromCookie(request);

        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    void getRefreshTokenFromCookie_TrowsJwtException_TokenNotFound() {

        assertThrows(JwtException.class, () -> jwtService.getRefreshTokenFromCookie(request));
    }

    @Test
    void getAccessTokenFromCookie() {

        request.setCookies(atCookie, rtCookie);

        var result = jwtService.getAccessTokenFromCookie(request);

        assertThat(result).isEqualTo(accessToken);
    }

    @Test
    void getAccessTokenFromCookie_TrowsJwtException_TokenNotFound() {

        assertThrows(JwtException.class, () -> jwtService.getAccessTokenFromCookie(request));
    }

    @Test
    void cleanRefreshTokenCookie() {
        var cleanedCookie = ResponseCookie
                .from(properties.getRtCookieName(), "")
                .path("/")
                .maxAge(properties.getRtExpirationInMs())
                .httpOnly(true)
                .build();

        var result = jwtService.cleanRefreshTokenCookie();

        assertThat(result).isEqualTo(cleanedCookie);
    }

    @Test
    void cleanAccessTokenCookie() {
        var cleanedCookie = ResponseCookie
                .from(properties.getAtCookieName(), "")
                .path("/")
                .maxAge(properties.getAtExpirationInMs())
                .httpOnly(true)
                .build();

        var result = jwtService.cleanAccessTokenCookie();

        assertThat(result).isEqualTo(cleanedCookie);
    }

    private String generateJwtToken(String subject, Date exparationDate, List<AppUserRole> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("userRoles", roles.stream()
                        .map(role -> role.getName().toUpperCase()).collect(Collectors.toList()))
                .setExpiration(exparationDate)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = properties.getJwtSecret().getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}