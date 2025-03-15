package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyPresetControllerApi;
import org.lets_play_be.dto.lobbyDto.ChangeLobbyPresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
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
    public ResponseEntity<LobbyPresetFullResponse> updateUserLobbyPreset(UpdateLobbyTitleAndTimeRequest request, Authentication authentication) {
        return null;
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
}
