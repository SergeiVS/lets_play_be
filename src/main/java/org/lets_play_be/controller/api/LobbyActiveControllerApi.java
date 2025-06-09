package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.lets_play_be.security.model.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/lobby/active")
public interface LobbyActiveControllerApi {
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
                                                                 NewActiveLobbyRequest request,
                                                                 Authentication authentication);

    @Operation(summary = "Adding new Active Lobby")
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
    @DeleteMapping
    ResponseEntity<ActiveLobbyResponse> deleteActiveLobby(@RequestParam("id") Long lobbyId, Authentication auth);
}
