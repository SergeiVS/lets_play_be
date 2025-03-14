package org.lets_play_be.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.security.utils.AppUserDetailsService;
import org.lets_play_be.security.utils.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (isRequestToSwagger(request)) return;

            handleFilterInternal(request);
        } catch (JwtException e) {
            logJwtException(e);
        } catch (IllegalArgumentException e) {
            log.error("Jwt token is empty: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private void handleFilterInternal(HttpServletRequest request) {
        String jwt = getAtJwtFromCookie(request);
        String refreshJwt = getRefreshJwtFromCookie(request);

        if (jwt == null || refreshJwt == null) return;

        String username = jwtService.getUsernameFromToken(jwt);

        if (username == null) return;

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!isRefreshTokenBlacklisted(refreshJwt) && isValidTokenAndNotAuthenticated(jwt, userDetails)) {
            validateCredentials(request, userDetails);
        }
    }

    private void validateCredentials(HttpServletRequest request, UserDetails userDetails) {
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private boolean isRefreshTokenBlacklisted(String token) {
        return jwtService.isRefreshTokenBlacklisted(token);
    }

    private boolean isValidTokenAndNotAuthenticated(String jwt, UserDetails userDetails) {
        return jwtService.validateToken(jwt, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private boolean isRequestToSwagger(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs");
    }

    private void logJwtException(JwtException e) {
        log.error(determineLogMsg(e), e.getMessage());
    }

    private String determineLogMsg(JwtException e) {
        return switch (e) {
            case SignatureException ignored -> "Signature exception: {}";
            case MalformedJwtException ignored -> "Malformed jwt token: {}";
            case ExpiredJwtException ignored -> "Expired jwt token: {}";
            case UnsupportedJwtException ignored -> "Unsupported jwt token: {}";
            default -> throw new RuntimeException("Unexpected error at JwtAuth: {}");
        };
    }

    private String getAtJwtFromCookie(HttpServletRequest request) {
        String jwt = null;

        if (request.getCookies() != null) {
            jwt = jwtService.getAccessTokenFromCookie(request);
        }

        return jwt;
    }

    private String getRefreshJwtFromCookie(HttpServletRequest request) {
        String jwt = null;

        if (request.getCookies() != null) {
            jwt = jwtService.getRefreshTokenFromCookie(request);
        }

        return jwt;
    }
}
