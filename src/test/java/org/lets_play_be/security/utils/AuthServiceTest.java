package org.lets_play_be.security.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.entity.BlacklistedToken;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.repository.BlacklistedTokenRepository;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.appUserService.GetUserProfileService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;
    @Mock
    private AppUserService userService;
    @Mock
    private SseLiveRecipientPool recipientPool;
    @Mock
    private JwtService jwtService;
    @Mock
    private GetUserProfileService userProfileService;
    @Mock
    private AppUserDetailsService userDetailsService;

    @InjectMocks
    AuthService authService;


    AppUser user;
    Authentication auth;
    LoginRequest loginRequest;
    AppUserRole role;
    AppUserProfile profile;
    ResponseCookie accessTokenCookie;
    ResponseCookie refreshTokenCookie;
    MockHttpServletRequest httpRequest;
    MockHttpServletResponse httpResponse;
    Date expiresAt;
    OffsetDateTime exparitionOffset;
    String exparitionString;

    @BeforeEach
    void setUp() {
        role = new AppUserRole(1L, UserRoleEnum.ROLE_USER.name());
        user = new AppUser(1L, "Name", "email@email.com", "Password", "N/A");
        user.getRoles().add(role);
        profile = new AppUserProfile(user);
        auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), profile.roles());
        loginRequest = new LoginRequest(user.getEmail(), user.getPassword());

        accessTokenCookie = ResponseCookie.from("access-token", "access-token")
                .path("/")
                .maxAge(5000)
                .httpOnly(true)
                .build();

        refreshTokenCookie = ResponseCookie.from("refresh-token", "refresh-token")
                .path("/")
                .maxAge(15000)
                .httpOnly(true)
                .build();

        httpResponse = new MockHttpServletResponse();
        httpRequest = new MockHttpServletRequest();
        expiresAt = new Date();
        exparitionOffset = expiresAt.toInstant().atOffset(ZoneOffset.of("+01:00"));
        exparitionString = exparitionOffset.toString();
    }

    @AfterEach
    void tearDown() {
        user = null;
        auth = null;
        loginRequest = null;
        profile = null;
        accessTokenCookie = null;
        refreshTokenCookie = null;
        httpRequest = null;
        httpResponse = null;
        expiresAt = null;
    }

    @Test
    void login_Success() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userProfileService.getUserProfile(user.getEmail())).thenReturn(profile);
        when(jwtService.generateAccessTokenCookie(user.getEmail(), profile.roles())).thenReturn(accessTokenCookie);
        when(jwtService.generateRefreshTokenCookie(user.getEmail(), profile.roles())).thenReturn(refreshTokenCookie);
        when(jwtService.extractExpiration(anyString())).thenReturn(expiresAt);

        String exparitionString = expiresAt.toInstant().atOffset(ZoneOffset.of("+01:00")).toString();

        LoginResponse result = authService.login(loginRequest, httpResponse);

        assertThat(result.accessTokenExpiration()).isEqualTo(exparitionString);
        assertThat(httpResponse.getStatus()).isEqualTo(200);
        assertThat(httpResponse.getCookies().length).isEqualTo(2);
        assertThat(httpResponse.getCookies()[0].getName()).isEqualTo(accessTokenCookie.getName());
        assertThat(httpResponse.getCookies()[1].getName()).isEqualTo(refreshTokenCookie.getName());

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userProfileService, times(1)).getUserProfile(user.getEmail());
        verify(jwtService, times(1)).generateAccessTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(1)).generateRefreshTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(1)).extractExpiration(anyString());
    }

    @Test
    void login_Throws_BadCredentials() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        assertThrows(RestException.class, () -> authService.login(loginRequest, httpResponse));
        assertThat(httpResponse.getCookies().length).isEqualTo(0);

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userProfileService, times(0)).getUserProfile(user.getEmail());
        verify(jwtService, times(0)).generateAccessTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(0)).generateRefreshTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(0)).extractExpiration(anyString());
    }

    @Test
    void login_Throws_UserNotFound() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userProfileService.getUserProfile(user.getEmail())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> authService.login(loginRequest, httpResponse));
        assertThat(httpResponse.getCookies().length).isEqualTo(0);

        verify(authManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userProfileService, times(1)).getUserProfile(user.getEmail());
        verify(jwtService, times(0)).generateAccessTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(0)).generateRefreshTokenCookie(user.getEmail(), profile.roles());
        verify(jwtService, times(0)).extractExpiration(anyString());
    }

    @Test
    void logout_Success() {
        when(jwtService.getRefreshTokenFromCookie(httpRequest)).thenReturn("refresh-token");
        when(jwtService.extractExpiration(anyString())).thenReturn(expiresAt);
        when(userService.getUserByEmailOrThrow(anyString())).thenReturn(user);
        when(recipientPool.isInPool(user.getId())).thenReturn(true);
        doNothing().when(recipientPool).removeRecipient(user.getId());
        when(blacklistedTokenRepository.save(any(BlacklistedToken.class))).thenReturn(null);
        when(jwtService.cleanAccessTokenCookie()).thenReturn(ResponseCookie.from("access-token", "")
                .path("/")
                .maxAge(5000)
                .httpOnly(true)
                .build());
        when(jwtService.cleanRefreshTokenCookie()).thenReturn(ResponseCookie.from("refresh-token", "")
                .path("/")
                .maxAge(5000)
                .httpOnly(true)
                .build());

        authService.logout(httpRequest, httpResponse, auth);

        assertThat(httpResponse.getStatus()).isEqualTo(200);
        assertThat(httpResponse.getCookies().length).isEqualTo(2);
        assertThat(httpResponse.getCookies()[0].getValue()).isEqualTo("");
        assertThat(httpResponse.getCookies()[1].getValue()).isEqualTo("");

        verify(jwtService, times(1)).getRefreshTokenFromCookie(httpRequest);
        verify(jwtService, times(1)).extractExpiration(anyString());
        verify(jwtService, times(1)).cleanAccessTokenCookie();
        verify(jwtService, times(1)).cleanRefreshTokenCookie();
        verify(userService, times(1)).getUserByEmailOrThrow(anyString());
        verify(recipientPool, times(1)).isInPool(user.getId());
        verify(recipientPool, times(1)).removeRecipient(user.getId());
        verify(blacklistedTokenRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void logout_AssertionError_TokenIsNull() {
        when(jwtService.getRefreshTokenFromCookie(httpRequest)).thenReturn(null);

        assertThrows(AssertionError.class, () -> authService.logout(httpRequest, httpResponse, auth));

        verify(jwtService, times(1)).getRefreshTokenFromCookie(httpRequest);
        verify(jwtService, times(0)).extractExpiration(anyString());
        verify(jwtService, times(0)).cleanAccessTokenCookie();
        verify(jwtService, times(0)).cleanRefreshTokenCookie();
        verify(userService, times(0)).getUserByEmailOrThrow(anyString());
        verify(recipientPool, times(0)).isInPool(user.getId());
        verify(recipientPool, times(0)).removeRecipient(user.getId());
        verify(blacklistedTokenRepository, times(0)).save(any(BlacklistedToken.class));
    }

    @Test
    void logout_Throws_UsernameNotFoundException() {
        when(jwtService.getRefreshTokenFromCookie(httpRequest)).thenReturn(refreshTokenCookie.getValue());
        when(jwtService.extractExpiration(anyString())).thenReturn(expiresAt);
        when(userService.getUserByEmailOrThrow(anyString())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> authService.logout(httpRequest, httpResponse, auth));

        verify(jwtService, times(1)).getRefreshTokenFromCookie(httpRequest);
        verify(jwtService, times(1)).extractExpiration(anyString());
        verify(userService, times(1)).getUserByEmailOrThrow(anyString());
        verify(jwtService, times(0)).cleanAccessTokenCookie();
        verify(jwtService, times(0)).cleanRefreshTokenCookie();
        verify(recipientPool, times(0)).isInPool(user.getId());
        verify(recipientPool, times(0)).removeRecipient(user.getId());
        verify(blacklistedTokenRepository, times(0)).save(any(BlacklistedToken.class));
    }

    @Test
    void refreshAccessToken() {
    }
}