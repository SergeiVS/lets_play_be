package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyActiveControllerApi;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.service.lobbyService.LobbyActiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LobbyActiveController implements LobbyActiveControllerApi {

    private final LobbyActiveService lobbyService;
    @Override
    public ResponseEntity<ActiveLobbyResponse> addNewLobbyActive(NewActiveLobbyRequest request,
                                                                 Authentication authentication) {
        ActiveLobbyResponse response = lobbyService.createActiveLobby(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
