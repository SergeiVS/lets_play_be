package org.lets_play_be.security.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.service.appUserService.GetUserProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private  final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final GetUserProfileService getUserProfileService;

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {

        Authentication authentication;
        authentication = getAuthentication(loginRequest);

        if (authentication.isAuthenticated()) {
            AppUserProfile userProfile = getUserProfileService.execute(authentication.getName());
            ResponseCookie accessTokenCookie = jwtService
                    .generateAccessTokenCookie(userProfile.email(), userProfile.roles());
            ResponseCookie refreshTokenCookie = jwtService
                    .generateRefreshTokenCookie(userProfile.email(), userProfile.roles());
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
            return new LoginResponse(accessTokenCookie.getValue());
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            throw new UsernameNotFoundException("Invalid UserRequest");
        }

    }

    private Authentication getAuthentication(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        } catch (BadCredentialsException e) {
            throw new RestException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}
