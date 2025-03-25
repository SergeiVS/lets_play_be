package org.lets_play_be.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/lobby/preset")
public interface LobbyPresetControllerApi {

    @PostMapping
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(@RequestBody
                                                                        @Valid
                                                                        NewLobbyPresetRequest request,
                                                                        Authentication authentication);

    @GetMapping
    public ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication);

    @PutMapping
    public ResponseEntity<UpdateLobbyTitleAndTimeResponse> updateLobbyTitleAndTime(@RequestBody
                                                                                   @NotNull
                                                                                   @Validated
                                                                                   UpdateLobbyTitleAndTimeRequest request);
//    @PostMapping("active")
//    public ResponseEntity<ActiveLobbyResponse> activatePreset(ActivateLobbyPresetRequest request);


    @PutMapping("users")
    public ResponseEntity<LobbyPresetFullResponse> addUsers(@RequestBody
                                                            @Valid
                                                            ChangeLobbyPresetUsersRequest request);

    @DeleteMapping("users")
    public ResponseEntity<LobbyPresetFullResponse> removeUsers(@RequestBody
                                                               @Valid
                                                               ChangeLobbyPresetUsersRequest request);

    @DeleteMapping("{id}")
    public ResponseEntity<StandardStringResponse> deletePreset(@PathVariable Long id);
}
