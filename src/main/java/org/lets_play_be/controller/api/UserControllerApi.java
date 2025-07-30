package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
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
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public interface UserControllerApi {

    @Operation(summary = "User can get data of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all users data",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppUserFullResponse.class))}
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @GetMapping
    ResponseEntity<List<AppUserFullResponse>> getAllUsers();

    @Operation(summary = "User can get his/herself data")
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
    @GetMapping("user")
    ResponseEntity<AppUserFullResponse> getUserData(Principal principal);

    @Operation(summary = "Updating of user Username and AvatarUrl")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user nickname and avatarUrl was updated ",
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
    @PatchMapping
    ResponseEntity<AppUserFullResponse> updateUserData(@RequestBody @Valid @NotNull UserDataUpdateRequest request,
                                                       @NotNull Principal principal);

    @Operation(summary = "User can change his/her Availability")
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
    @PatchMapping("availability")
    ResponseEntity<AppUserFullResponse> updateUserAvailability(@RequestBody @Valid @NotNull
                                                               UserAvailabilityUpdateRequest request, @NotNull Principal principal);
}
