package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyPresetControllerApi;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.service.lobbyService.LobbyBaseUpdateService;
import org.lets_play_be.service.lobbyService.LobbyPresetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LobbyPresetController implements LobbyPresetControllerApi {

    private final LobbyPresetService lobbyPresetService;
    private final LobbyBaseUpdateService lobbyBaseUpdateService;

    @Override
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(NewLobbyRequest request,
                                                                        Authentication authentication) {
        LobbyPresetFullResponse response = lobbyPresetService.createNewLobbyPreset(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication) {
        List<LobbyPresetFullResponse> responseList = lobbyPresetService.getAllUserPresets(authentication);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyPresetFullResponse> updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request,
                                                                                   Authentication auth) {
        var response = lobbyPresetService.updateLobbyTitleAndTime(request, auth);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyPresetFullResponse> addUsers(ChangeLobbyPresetUsersRequest request) {
        var response = lobbyPresetService.addNewUsersToLobbyPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyPresetFullResponse> removeUsers(ChangeLobbyPresetUsersRequest request) {
        var response = lobbyPresetService.removeUserFromPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StandardStringResponse> deletePreset(Long id) {
        var response = lobbyPresetService.removeLobbyPreset(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
