package org.lets_play_be.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
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
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {

            if (request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = getAtJwtFromCookie(request);

            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.getUsernameFromToken(jwt);

            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(jwt, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (SignatureException e) {
            log.error("Invalid jwt signature: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (MalformedJwtException e) {
            log.error("Invalid jwt token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (ExpiredJwtException e) {
            log.error("Expired jwt token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported jwt token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (IllegalArgumentException e) {
            log.error("Jwt token is empty: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String getAtJwtFromCookie(HttpServletRequest request) {

        String jwt = null;

        if (request.getCookies() != null) {
            jwt = jwtService.getAccessTokenFromCookie(request);
        }
        return jwt;
    }
}
