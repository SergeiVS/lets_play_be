package org.lets_play_be.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/lobby")
public interface LobbyPresetControllerApi {

    @PostMapping("/preset")
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(@RequestBody
                                                                        @Valid
                                                                        NewLobbyPresetRequest request,
                                                                        Authentication authentication);

    @GetMapping("preset")
    public ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication);

    @PutMapping("{id}")
    public ResponseEntity<UpdateLobbyTitleAndTimeResponse> updateLobbyTitleAndTime(@RequestBody
                                                                                   @NotNull
                                                                                   @Valid
                                                                                   UpdateLobbyTitleAndTimeRequest request);


    @PutMapping("preset/users")
    public ResponseEntity<LobbyPresetFullResponse> addUsers(@RequestBody
                                                            @Valid
                                                            ChangeLobbyPresetUsersRequest request);

    @DeleteMapping("preset/users")
    public ResponseEntity<LobbyPresetFullResponse> removeUsers(@RequestBody
                                                               @Valid
                                                               ChangeLobbyPresetUsersRequest request);

    @DeleteMapping("preset/{id}")
    public ResponseEntity<StandardStringResponse> deletePreset(@PathVariable Long id);
}
