package org.lets_play_be.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.BlacklistedToken;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.repository.BlacklistedTokenRepository;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.appUserService.GetUserProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final AppUserService userService;
    private final SseLiveRecipientPool recipientPool;
    private final JwtService jwtService;
    private final GetUserProfileService getUserProfileService;
    private final AppUserDetailsService userDetailsService;


    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        var auth = getAuthentication(loginRequest);

        if (auth.isAuthenticated()) {

            var accessTokenCookie = setResponseCookies(response, auth);
            var tokenExpiration = getTokenExpirationFromToken(accessTokenCookie.getValue());

            return new LoginResponse(tokenExpiration.toString());

        } else {
            SecurityContextHolder.getContext().setAuthentication(null);

            throw new RestException("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {

        final var refreshToken = jwtService.getRefreshTokenFromCookie(request);

        assert refreshToken != null : "Refresh token is null";

        final var tokenExpiration = getTokenExpirationFromToken(refreshToken);
        final var user = userService.getUserByEmailOrThrow(auth.getName());

        removeSseRecipient(user);

        cleanTokens(response, user, refreshToken, tokenExpiration);
    }

    public LoginResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        final var refreshToken = jwtService.getRefreshTokenFromCookie(request);
        final var userEmail = jwtService.getUsernameFromToken(refreshToken);
        final var userDetails = userDetailsService.loadUserByUsername(userEmail);

        isTokenValid(refreshToken, userDetails);

        var atCookie = getNewAtCookie(userEmail);

        response.addHeader(HttpHeaders.SET_COOKIE, atCookie.toString());

        var tokenExpiration = getTokenExpirationFromToken(atCookie.getValue());

        return new LoginResponse(tokenExpiration.toString());
    }

    private ResponseCookie getNewAtCookie(String userEmail) {

        final var profile = getUserProfileService.getUserProfile(userEmail);

        return jwtService.generateAccessTokenCookie(userEmail, profile.roles());
    }

    private void isTokenValid(String refreshToken, UserDetails userDetails) {

        try {
            var isValid = jwtService.validateToken(refreshToken, userDetails);

            if (!isValid) {
                throw new RestException("Refresh token is not valid", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            throw new RestException(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    private ResponseCookie setResponseCookies(HttpServletResponse response, Authentication authentication) {

        var userProfile = getUserProfileService.getUserProfile(authentication.getName());
        var accessTokenCookie = jwtService.generateAccessTokenCookie(userProfile.email(), userProfile.roles());
        var refreshTokenCookie = jwtService.generateRefreshTokenCookie(userProfile.email(), userProfile.roles());

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return accessTokenCookie;
    }

    private OffsetDateTime getTokenExpirationFromToken(String token) {
        return jwtService.extractExpiration(token).toInstant().atOffset(ZoneOffset.of("+01:00"));
    }


    private Authentication getAuthentication(LoginRequest loginRequest) {

        Authentication authentication;
        var email = normalizeEmail(loginRequest.email());
        var password = loginRequest.password().trim();

        try {
            authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        } catch (BadCredentialsException e) {

            throw new RestException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private void removeSseRecipient(AppUser user) {

        if (recipientPool.isInPool(user.getId())) {
            recipientPool.removeRecipient(user.getId());
        }
    }

    private void cleanTokens(HttpServletResponse response, AppUser user, String refreshToken, OffsetDateTime expiry) {

        blacklistedTokenRepository.save(new BlacklistedToken(user, refreshToken, expiry));

        response.addHeader(HttpHeaders.SET_COOKIE, jwtService.cleanAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtService.cleanRefreshTokenCookie().toString());
    }

}
