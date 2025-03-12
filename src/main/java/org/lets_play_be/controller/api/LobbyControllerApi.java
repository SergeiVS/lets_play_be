package org.lets_play_be.controller.api;

import jakarta.validation.Valid;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/lobby")
public interface LobbyControllerApi {

    @PostMapping
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(@RequestBody
                                                                        @Valid NewLobbyPresetRequest request,
                                                                        Authentication authentication);

    @GetMapping
    public ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication);
}
