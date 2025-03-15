package org.lets_play_be.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.entity.BlacklistedToken;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.BlacklistedTokenRepository;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.service.appUserService.AppUserRepositoryService;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.lets_play_be.utils.FormattingUtils.NORMALIZE_EMAIL;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final AppUserRepositoryService appUserRepositoryService;

    private final JwtService jwtService;
    private final GetUserProfileService getUserProfileService;
    private final AppUserDetailsService userDetailsService;

    public void logout(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        final String refreshToken = jwtService.getRefreshTokenFromCookie(request);

        final OffsetDateTime expiry = getTokenExpirationFromToken(refreshToken);

        final var user = appUserRepositoryService.findByEmail(principal.getName());

        if (user.isPresent()) {
            blacklistedTokenRepository.save(new BlacklistedToken(user.get(), refreshToken, expiry));

            response.addHeader(HttpHeaders.SET_COOKIE, jwtService.cleanAccessTokenCookie().toString());
            response.addHeader(HttpHeaders.SET_COOKIE, jwtService.cleanRefreshTokenCookie().toString());
        } else {
            throw new UsernameNotFoundException("Invalid UserRequest");
        }
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication;
        authentication = getAuthentication(loginRequest);

        if (authentication.isAuthenticated()) {
            return getLoginResponse(response, authentication);
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new UsernameNotFoundException("Invalid UserRequest");
        }

    }

    public LoginResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        final String refreshToken = jwtService.getAccessTokenFromCookie(request);

        final String userEmail = jwtService.getUsernameFromToken(refreshToken);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        isTokenExpired(refreshToken);

        isTokenValid(refreshToken, userDetails);

        setNewATIntoCookie(response, userEmail);

        return new LoginResponse(jwtService.extractExpiration(jwtService.getAccessTokenFromCookie(request)).toString());
    }

    private void setNewATIntoCookie(HttpServletResponse response, String userEmail) {
        final AppUserProfile profile = getUserProfileService.getUserProfile(userEmail);
        final ResponseCookie cookie = jwtService.generateAccessTokenCookie(userEmail, profile.roles());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void isTokenValid(String refreshToken, UserDetails userDetails) {
        if (!jwtService.validateToken(refreshToken, userDetails)) {
            throw new RestException("Refresh token is not valid", HttpStatus.FORBIDDEN);
        }
    }

    private void isTokenExpired(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new RestException("Refresh token expired", HttpStatus.FORBIDDEN);
        }
    }

    private LoginResponse getLoginResponse(HttpServletResponse response, Authentication authentication) {

        AppUserProfile userProfile = getUserProfileService.getUserProfile(authentication.getName());
        ResponseCookie accessTokenCookie = jwtService.generateAccessTokenCookie(userProfile.email(), userProfile.roles());
        ResponseCookie refreshTokenCookie = jwtService.generateRefreshTokenCookie(userProfile.email(), userProfile.roles());
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        OffsetDateTime tokenExpiration = getTokenExpiration(accessTokenCookie);

        return new LoginResponse(tokenExpiration.toString());
    }

    private OffsetDateTime getTokenExpiration(ResponseCookie accessTokenCookie) {
        return getTokenExpirationFromToken(accessTokenCookie.getValue());
    }

    private OffsetDateTime getTokenExpirationFromToken(String token) {
        return jwtService.extractExpiration(token).toInstant().atOffset(ZoneOffset.of("+01:00"));
    }


    private Authentication getAuthentication(LoginRequest loginRequest) {

        Authentication authentication;
        String email = NORMALIZE_EMAIL(loginRequest.email());
        String password = loginRequest.password().trim();

        try {
            authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new RestException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
