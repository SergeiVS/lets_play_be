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
    ResponseEntity<PresetFullResponse> createNewLobbyPreset(@RequestBody
                                                                 @Valid
                                                                 NewPresetRequest request,
                                                            Authentication authentication);
    @Deprecated
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
    ResponseEntity<List<PresetFullResponse>> getAllUserLobbyPresets(Authentication authentication);

    @Operation(summary = "Getting saved users lobby preset. If not found returns new blank lobby preset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preset were found or created",
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
    @GetMapping("/{userId}")
    ResponseEntity<PresetFullResponse> getUsersLobbyPreset(@PathVariable @NotNull long userId, Authentication auth);

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
    ResponseEntity<PresetFullResponse> updateLobbyTitleAndTime(@RequestBody
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
    ResponseEntity<PresetFullResponse> addUsers(@RequestBody
                                                     @Valid
                                                ChangePresetUsersRequest request, Authentication auth);

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
    ResponseEntity<PresetFullResponse> removeUsers(@RequestBody
                                                        @Valid
                                                   ChangePresetUsersRequest request, Authentication auth);

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
    ResponseEntity<StandardStringResponse> deletePreset(@PathVariable("id") Long presetId, Authentication auth);
}
