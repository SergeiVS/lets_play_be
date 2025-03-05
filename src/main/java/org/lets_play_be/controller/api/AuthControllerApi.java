package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.lets_play_be.security.model.LoginRequest;
import org.lets_play_be.security.model.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public interface AuthControllerApi {

    @Operation(summary = "Authenticate user by email an password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Is logged on",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
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
            @RequestBody @Valid @NotNull LoginRequest loginRequest, HttpServletResponse response);

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
    ResponseEntity<AppUserFullResponse> register(@RequestBody @Valid @NotNull NewUserRegistrationRequest request);

    @Operation(summary = "Refresh access token if expire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token issued")
    })
    @GetMapping("refresh")
    ResponseEntity<LoginResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
}
