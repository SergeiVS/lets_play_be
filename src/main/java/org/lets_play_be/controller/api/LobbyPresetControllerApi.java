package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/lobby/preset")
public interface LobbyPresetControllerApi {

    @Operation(summary = "Creating a new Preset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preset is created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
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
    @PostMapping
    ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(@RequestBody
                                                                 @Valid
                                                                 NewLobbyRequest request,
                                                                 Authentication authentication);

    @Operation(summary = "Getting All Users Presets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presets are found, may return an empty list",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
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
    @GetMapping
    ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication);

    @Operation(summary = "Updating the Presets Time and Title fields")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "preset Updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
            @ApiResponse(responseCode = "400", description = " User or Preset not found",
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
    @PatchMapping
    ResponseEntity<LobbyPresetFullResponse> updateLobbyTitleAndTime(@RequestBody
                                                                    @NotNull
                                                                    @Validated
                                                                    UpdateLobbyTitleAndTimeRequest request,
                                                                    Authentication auth);

    @Operation(summary = "Adding Users to Presets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
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
    @PutMapping("user")
    ResponseEntity<LobbyPresetFullResponse> addUsers(@RequestBody
                                                     @Valid
                                                     ChangeLobbyPresetUsersRequest request);

    @Operation(summary = "Users deleting from preset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
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
    @DeleteMapping("user")
    ResponseEntity<LobbyPresetFullResponse> removeUsers(@RequestBody
                                                        @Valid
                                                        ChangeLobbyPresetUsersRequest request);

    @Operation(summary = "Deleting preset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preset was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
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
    @DeleteMapping("{id}")
    ResponseEntity<StandardStringResponse> deletePreset(@PathVariable Long id);
}
