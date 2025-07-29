package org.lets_play_be.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.errorDto.ErrorResponse;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.exception.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/invite")
public interface InviteControllerApi {

    @Operation(summary = "Getting all User invites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invites were found or List is empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InviteResponse.class))}),
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
    @GetMapping("user/{id}")
    ResponseEntity<List<InviteResponse>> getAllUserInvitesByUser(@PathVariable("id") @NotNull long userId);

    @Operation(summary = "Getting all invites of current lobby")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invites was found or List is empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InviteResponse.class))}),
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
    @GetMapping("lobby/{id}")
    ResponseEntity<List<InviteResponse>> getAllInvitesByLobby(@PathVariable("id") @NotNull long lobbyId);

    @Operation(summary = "Update invite state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invites state was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InviteResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PatchMapping("user")
    ResponseEntity<InviteResponse> deleteInvite(@RequestBody UpdateInviteStateRequest request);

    @Operation(summary = "Update invite isSeen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invites state was updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InviteResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PatchMapping("user/{id}")
    ResponseEntity<StandardStringResponse> updateInviteISeen(@PathVariable("id") long inviteId, Authentication auth);


    @Operation(summary = "Deletes invite, User should be lobby owner only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "invite was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InviteResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User is not authenticated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access is denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping({"{id}"})
    ResponseEntity<InviteResponse> deleteInvite(@PathVariable("id") @NotNull long inviteId, Authentication auth);

}
