package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyPresetControllerApi;
import org.lets_play_be.dto.lobbyDto.ChangeLobbyPresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
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

    @Override
    public ResponseEntity<LobbyPresetFullResponse> createNewLobbyPreset(NewLobbyPresetRequest request,
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
    public ResponseEntity<LobbyPresetFullResponse> addUsers(ChangeLobbyPresetUsersRequest request) {
        LobbyPresetFullResponse response = lobbyPresetService.addNewUsersToLobbyPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyPresetFullResponse> removeUsers(ChangeLobbyPresetUsersRequest request) {
        LobbyPresetFullResponse response = lobbyPresetService.removeUserFromPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
