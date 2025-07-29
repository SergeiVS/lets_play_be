package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/lobby/active")
public interface LobbyActiveControllerApi {

    @Deprecated
    @Operation(summary = "Adding new Active Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby were created",
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
    ResponseEntity<ActiveLobbyResponse> addNewLobbyActive(@RequestBody
                                                          @Validated
                                                          @NotNull
                                                          NewActiveLobbyRequest request,
                                                          Authentication auth);

    @Operation(summary = "Creating new Active Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby were created",
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
    @PostMapping("new")
    ResponseEntity<ActiveLobbyResponse> createActiveLobby(ActivatePresetRequest request,
                                                          Authentication auth);

    @Operation(summary = "Returns current lobby for authenticated user or if not found returns http status 404 NOT FOUND")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns LobbyResponse or null",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Lobby not found",
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
    ResponseEntity<ActiveLobbyResponse> getUsersActiveLobby(Authentication auth);

    @Operation(summary = "Updating Title and Time fields in Active Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Lobby not found",
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
    ResponseEntity<ActiveLobbyResponse> updateLobbyActiveTitleAndTile(@RequestBody @Valid UpdateLobbyTitleAndTimeRequest request, Authentication auth);

    @Operation(summary = "invite new users to Active Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActiveLobbyResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Lobby not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PatchMapping("users")
    ResponseEntity<ActiveLobbyResponse> inviteNewUsers(@RequestBody @Valid InviteNewUsersRequest request, Authentication auth);

    @Operation(summary = "Delete lobby by owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby were Deleted",
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
    ResponseEntity<ActiveLobbyResponse> deleteActiveLobby(@PathVariable("id") @NotNull Long lobbyId, Authentication auth);

}
