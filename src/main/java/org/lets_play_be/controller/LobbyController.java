package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyControllerApi;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetResponse;
import org.lets_play_be.service.lobbyService.LobbyPresetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LobbyController implements LobbyControllerApi {

    private final LobbyPresetService lobbyPresetService;

    @Override
    public ResponseEntity<NewLobbyPresetResponse> createNewLobbyPreset(NewLobbyPresetRequest request, Authentication authentication) {
        NewLobbyPresetResponse response = lobbyPresetService.createNewLobbyPreset(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
