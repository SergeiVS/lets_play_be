package org.lets_play_be.controller.api;

import jakarta.validation.Valid;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/lobby")
public interface LobbyControllerApi {

    @PostMapping
    public ResponseEntity<NewLobbyPresetResponse> createNewLobbyPreset(@RequestBody
                                                                       @Valid NewLobbyPresetRequest request,
                                                                       Authentication authentication);
}
