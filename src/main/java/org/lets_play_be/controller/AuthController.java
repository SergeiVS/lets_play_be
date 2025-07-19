package org.lets_play_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.AuthControllerApi;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.security.utils.AuthService;
import org.lets_play_be.service.appUserService.RegisterNewUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;
    private final RegisterNewUserService registerNewUserService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest, response);
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AppUserFullResponse> register(NewUserRegistrationRequest request) {
        AppUserFullResponse newUserResponse = registerNewUserService.registerNewUser(request);
        return new ResponseEntity<>(newUserResponse, HttpStatus.OK);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        authService.logout(request, response, auth);
    }

    @Override
    public ResponseEntity<LoginResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(authService.refreshAccessToken(request, response), HttpStatus.OK);
    }

}
