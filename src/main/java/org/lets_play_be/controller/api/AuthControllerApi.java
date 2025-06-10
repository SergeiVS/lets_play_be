package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/auth")
public interface AuthControllerApi {

    @Operation(summary = "Authenticate user by email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Is logged in",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Credantials",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("login")
    ResponseEntity<LoginResponse> login(
            @RequestBody @Validated @NotNull LoginRequest loginRequest, HttpServletResponse response);

    @Operation(summary = "Registering a new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Is registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid validation failure",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "409", description = "User already exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("register")
    ResponseEntity<AppUserFullResponse> register(@RequestBody @Validated @NotNull NewUserRegistrationRequest request);

    @Operation(summary = "logging out")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "logged out",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))})
    })
    @PostMapping("logout")
    void logout(HttpServletRequest request, HttpServletResponse response, Principal principal);

    @Operation(summary = "Refresh access token if expire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token issued")
    })
    @GetMapping("refresh")
    ResponseEntity<LoginResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
}
