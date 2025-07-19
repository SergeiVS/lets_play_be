package org.lets_play_be.security.filter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.security.utils.AppUserDetailsService;
import org.lets_play_be.security.utils.JwtService;
import org.lets_play_be.security.utils.UserDetailsMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AppUserDetailsService detailsService;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain chain;
    private String email;
    private AppUser user;
    private AppUserRole role;
    private UserDetailsMapper userDetails;
    private String accessToken;
    private String refreshToken;
    private Cookie atCookie;
    private Cookie rtCookie;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        email = "test@test.com";
        role = new AppUserRole("ROLE_USER");
        user = new AppUser("", email, "", "");
        user.getRoles().add(role);
        userDetails = new UserDetailsMapper(user);
        accessToken = "access_token";
        refreshToken = "refresh_token";
        atCookie = new Cookie("access_token", accessToken);
        rtCookie = new Cookie("refresh_token", refreshToken);
        request.setCookies(atCookie, rtCookie);
    }

    @AfterEach
    void tearDown() {
        request = null;
        response = null;
        chain = null;
        user = null;
        role = null;
        userDetails = null;
        accessToken = null;
        refreshToken = null;
        rtCookie = null;
        atCookie = null;
    }

    @ParameterizedTest
    @ValueSource(strings = {"/swagger-ui", "/v3/api-docs"})
    void doFilterInternal_RequestToSwagger(String url) throws ServletException, IOException {

        request.setRequestURI(url);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        verifyNoInteractions(jwtService);
        verifyNoInteractions(detailsService);
    }

    @Test
    void doFilterInternal_AccessToken_Null() throws ServletException, IOException {
        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(detailsService);
    }

    @Test
    void doFilterInternal_RefreshToken_Null() throws ServletException, IOException {
        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(detailsService);
    }

    @Test
    void doFilterInternal_NoUsernameNotFound() throws ServletException, IOException {
        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verifyNoMoreInteractions(jwtService);
        verifyNoInteractions(detailsService);
    }

    @Test
    void doFilterInternal_UserDetailsNotFound() throws ServletException, IOException {

        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(email);
        when(detailsService.loadUserByUsername(email)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();


        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verify(detailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isRefreshTokenBlacklisted(refreshToken);
        verify(jwtService, times(1)).validateToken(accessToken, null);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(detailsService);
    }

    @Test
    void doFilterInternal_RT_Blacklisted() throws ServletException, IOException {

        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(email);
        when(detailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isRefreshTokenBlacklisted(refreshToken)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verify(detailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isRefreshTokenBlacklisted(refreshToken);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(detailsService);
    }

    @Test
    void doFilterInternal_AT_NotValid() throws ServletException, IOException {

        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(email);
        when(detailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isRefreshTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtService.validateToken(accessToken, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verify(detailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isRefreshTokenBlacklisted(refreshToken);
        verify(jwtService, times(1)).validateToken(accessToken, userDetails);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(detailsService);
    }

    @Test
    void doFilterInternal_AT_Expired() {

        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(email);
        when(detailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isRefreshTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtService.validateToken(accessToken, userDetails)).thenThrow(JwtException.class);

        assertThrows(RuntimeException.class, () -> jwtAuthenticationFilter.doFilterInternal(request, response, chain), "Token is expired");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verify(detailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isRefreshTokenBlacklisted(refreshToken);
        verify(jwtService, times(1)).validateToken(accessToken, userDetails);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(detailsService);
    }

    @Test
    void doFilterInternal_Success() throws ServletException, IOException {

        when(jwtService.getAccessTokenFromCookie(request)).thenReturn(accessToken);
        when(jwtService.getRefreshTokenFromCookie(request)).thenReturn(refreshToken);
        when(jwtService.getUsernameFromToken(accessToken)).thenReturn(email);
        when(detailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isRefreshTokenBlacklisted(refreshToken)).thenReturn(false);
        when(jwtService.validateToken(accessToken, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass()).isEqualTo(UserDetailsMapper.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();

        verify(jwtService, times(1)).getAccessTokenFromCookie(request);
        verify(jwtService, times(1)).getRefreshTokenFromCookie(request);
        verify(jwtService, times(1)).getUsernameFromToken(accessToken);
        verify(detailsService, times(1)).loadUserByUsername(email);
        verify(jwtService, times(1)).isRefreshTokenBlacklisted(refreshToken);
        verify(jwtService, times(1)).validateToken(accessToken, userDetails);
        verifyNoMoreInteractions(jwtService);
        verifyNoMoreInteractions(detailsService);
    }
}