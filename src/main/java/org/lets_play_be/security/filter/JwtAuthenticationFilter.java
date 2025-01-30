package org.lets_play_be.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.security.utils.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = getAtJwtFromCookie(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = getUserNameFromJwtOrThrow(request, response, filterChain, jwt);

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(jwt, userDetails) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getUserNameFromJwtOrThrow(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String jwt) throws ServletException, IOException {
        String username = null;
        try {
            username = jwtService.getUsernameFromToken(jwt);
        } catch (SignatureException e) {
//            log.error("Invalid jwt signature: {}", e.getMessage());
            doFilterInternal(request, response, filterChain);
        } catch (MalformedJwtException e) {
//            log.error("Invalid jwt token: {}", e.getMessage());
            doFilterInternal(request, response, filterChain);
        } catch (ExpiredJwtException e) {
            log.error("Expired jwt token: {}", e.getMessage());
            doFilterInternal(request, response, filterChain);
        } catch (UnsupportedJwtException e) {
//            log.error("Unsupported jwt token: {}", e.getMessage());
            doFilterInternal(request, response, filterChain);
        } catch (IllegalArgumentException e) {
//            log.error("Jwt token is empty: {}", e.getMessage());
            doFilterInternal(request, response, filterChain);
        }
        return username;
    }

    private String getAtJwtFromCookie(HttpServletRequest request) {

        String jwt = null;

        if (request.getCookies() != null) {
            jwt = jwtService.getAccessTokenFromCookie(request);
        }
        return jwt;
    }
}
