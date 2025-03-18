package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyPresetControllerApi;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.service.lobbyService.LobbyBaseUpdateService;
import org.lets_play_be.service.lobbyService.LobbyPresetCRUDService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LobbyPresetController implements LobbyPresetControllerApi {

    private final LobbyPresetCRUDService lobbyPresetCRUDService;
    private final LobbyBaseUpdateService lobbyBaseUpdateService;

    @Override
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(NewLobbyPresetRequest request,
                                                                        Authentication authentication) {
        LobbyPresetFullResponse response = lobbyPresetCRUDService.createNewLobbyPreset(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<LobbyPresetFullResponse>> getAllUserLobbyPresets(Authentication authentication) {
        List<LobbyPresetFullResponse> responseList = lobbyPresetCRUDService.getAllUserPresets(authentication);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UpdateLobbyTitleAndTimeResponse> updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {
        UpdateLobbyTitleAndTimeResponse response = lobbyBaseUpdateService.updateLobbyTitleAndTime(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ActiveLobbyResponse> activatePreset(ActivateLobbyPresetRequest request) {
        ActiveLobbyResponse response   = lobbyPresetCRUDService.activateLobbyPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<LobbyPresetFullResponse> addUsers(ChangeLobbyPresetUsersRequest request) {
        LobbyPresetFullResponse response = lobbyPresetCRUDService.addNewUsersToLobbyPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyPresetFullResponse> removeUsers(ChangeLobbyPresetUsersRequest request) {
        LobbyPresetFullResponse response = lobbyPresetCRUDService.removeUserFromPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StandardStringResponse> deletePreset(Long id) {
        StandardStringResponse response = lobbyPresetCRUDService.removeLobbyPreset(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
