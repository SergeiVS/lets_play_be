package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.lobbyDto.ActivateLobbyRequest;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyRequest;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/lobby")
public interface LobbyControllerApi {

    @Operation(summary = "Activates Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby were activated",
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("activate")
    ResponseEntity<LobbyResponse> activateLobby(
            @RequestBody
            @Valid
            ActivateLobbyRequest request,
            Authentication auth);

    @Operation(summary = "Returns current lobby for authenticated user ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns LobbyResponse or null",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping
    ResponseEntity<LobbyResponse> getLobby(
            Authentication auth
    );

    @Operation(summary = "Updating Title and Time fields in User's Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping
    ResponseEntity<LobbyResponse> updateLobbyData(
            @RequestBody
            @Valid
            UpdateLobbyRequest request,
            Authentication auth);

    @Operation(summary = "invite new users to Active Lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("users/invite")
    ResponseEntity<LobbyResponse> inviteNewUsers(
            @RequestBody
            @Valid
            ChangeUsersListRequest request,
            Authentication auth);

    @Operation(summary = "Leave an active Lobby by invited user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("leave/{lobbyId}")
    ResponseEntity<LobbyResponse> leaveLobby(
            @PathVariable("lobbyId")
            long lobbyId,
            Authentication auth);

    @Operation(summary = "Kick users from Active Lobby by lobby owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LobbyResponse.class))}),
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("users/kick")
    ResponseEntity<LobbyResponse> kickUsers(
            @RequestBody
            @Valid
            ChangeUsersListRequest request,
            Authentication auth);

    @Operation(summary = "Deactivate lobby by owner")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lobby were Deactivated",
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
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PatchMapping("deactivate/{lobbyId}")
    ResponseEntity<LobbyResponse> deactivateLobby(
            @PathVariable("lobbyId")
            @NotNull
            Long lobbyId,
            Authentication auth);

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
    @PutMapping("users/add")
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
    @DeleteMapping("users/remove")
    ResponseEntity<LobbyResponse> removeUsers(
            @RequestBody
            @Valid
            ChangeUsersListRequest request,
            Authentication auth
    );
}
