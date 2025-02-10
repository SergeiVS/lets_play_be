package org.lets_play_be.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.AuthControllerApi;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.lets_play_be.security.utils.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController  implements AuthControllerApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse= authService.login(loginRequest, response);
       return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

}
