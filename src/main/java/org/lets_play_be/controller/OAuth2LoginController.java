package org.lets_play_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.security.utils.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuth2LoginController {

    private final AuthService authService;

    @GetMapping("/oauth2/login-success")
    public ResponseEntity<LoginResponse> oauth2LoginSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth) {
        var loginResponse = authService.oAuthLoginSetJwt(request, response, auth);

        return ResponseEntity.ok(loginResponse);
    }
}
