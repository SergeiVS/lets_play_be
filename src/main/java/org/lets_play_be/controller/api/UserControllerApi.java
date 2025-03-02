package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/user")
public interface UserControllerApi {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get user data of authenticated user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Some of Arguments is illegal",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping
    ResponseEntity<AppUserFullResponse> getUserData(Principal principal);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update user nickname and avatarUrl",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Some of Arguments is illegal",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping
    ResponseEntity<AppUserFullResponse> updateUserData(@RequestBody @Valid @NotNull UserDataUpdateRequest request,
                                                       @NotNull Principal principal);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change user availability",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "Some of Arguments is illegal",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PutMapping("availability")
    ResponseEntity<AppUserFullResponse> updateUserAvailability(@RequestBody @Valid @NotNull
                                                               UserAvailabilityUpdateRequest request, @NotNull Principal principal);
}
