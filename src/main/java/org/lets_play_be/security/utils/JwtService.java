package org.lets_play_be.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.AppUserRole;
import org.lets_play_be.security.securityConfig.JwtProperties;
import org.springframework.cglib.core.internal.Function;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties config;

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(String email, List<AppUserRole> userRoles) {
        Date expireAt = new Date(System.currentTimeMillis() + config.getAtExpirationInMs());
        return generateJwtToken(email, expireAt, userRoles);
    }

    public String generateRefreshToken(String email, List<AppUserRole> userRoles) {
        Date expireAt = new Date(System.currentTimeMillis() + config.getRtExpirationInMs());
        return generateJwtToken(email, expireAt, userRoles);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public ResponseCookie generateAccessTokenCookie(String email, List<AppUserRole> roles) {
        String jwt = generateAccessToken(email, roles);
        return generateCookie(config.getAtCookieName(), jwt, config.getAtExpirationInMs());
    }

    public ResponseCookie generateRefreshTokenCookie(String email, List<AppUserRole> roles) {
        String jwt = generateRefreshToken(email, roles);
        return generateCookie(config.getRtCookieName(), jwt, config.getRtExpirationInMs());
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, config.getRtCookieName());
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, config.getAtCookieName());
    }

    public ResponseCookie cleanRefreshTokenCookie(){
        return generateCookie(config.getRtCookieName(), "", config.getRtExpirationInMs());
    }

    public ResponseCookie cleanAccessTokenCookie(){
        return generateCookie(config.getAtCookieName(), "", config.getAtExpirationInMs());
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
        byte[] keyBytes = config.getJwtSecret().getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private ResponseCookie generateCookie(String name, String value, int maxAge) {
        return ResponseCookie
                .from(name, value)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

}
