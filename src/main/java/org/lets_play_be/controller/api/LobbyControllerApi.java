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
public interface LobbyControllerApi {

    @Deprecated
    @Operation(summary = "Adding new Active Lobby")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby were created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping
    ResponseEntity<LobbyResponse> addNewLobbyActive(@RequestBody @Validated @NotNull NewActiveLobbyRequest request, Authentication auth);

    @Operation(summary = "Creating new Active Lobby")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby were created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("new")
    ResponseEntity<LobbyResponse> activateLobby(ActivatePresetRequest request, Authentication auth);

    @Operation(summary = "Returns current lobby for authenticated user or if not found returns http status 404 NOT FOUND")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns LobbyResponse or null", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "Lobby not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping
    ResponseEntity<LobbyResponse> getLobby(Authentication auth);

    @Operation(summary = "Updating Title and Time fields in Active Lobby")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby was updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "Lobby not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping
    ResponseEntity<LobbyResponse> updateLobbyData(@RequestBody @Valid UpdateLobbyRequest request, Authentication auth);

    @Operation(summary = "invite new users to Active Lobby")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby was updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "Lobby not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("users")
    ResponseEntity<LobbyResponse> inviteNewUsers(@RequestBody @Valid ChangeUsersListRequest request, Authentication auth);

    @Operation(summary = "Leave an active Lobby")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby was updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "Lobby not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("leave/{lobbyId}")
    ResponseEntity<PresetFullResponse> leaveLobby(@PathVariable("lobbyId") long lobbyId, Authentication auth);

    @Operation(summary = "Kick users from Active Lobby by lobby owner")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby was updated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "Lobby not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("kick")
    ResponseEntity<LobbyResponse> kickUsers(@RequestBody @Valid ChangeUsersListRequest request, Authentication auth);

    @Operation(summary = "Delete lobby by owner")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lobby were Deleted", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LobbyResponse.class))}), @ApiResponse(responseCode = "400", description = "Invalid input", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponse.class))}), @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}), @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @DeleteMapping("{lobbyId}")
    ResponseEntity<LobbyResponse> deactivateLobby(@PathVariable("lobbyId") @NotNull Long lobbyId, Authentication auth);

    @Operation(summary = "Adding users to an inactive lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
    ResponseEntity<LobbyResponse> addUsers(
            @RequestBody
            @Valid
            ChangeUsersListRequest request,
            Authentication auth
    );

    @Operation(summary = "Deleting users from an inactive Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
    ResponseEntity<LobbyResponse> removeUsers(
            @RequestBody
            @Valid
            ChangeUsersListRequest request,
            Authentication auth
    );
}
