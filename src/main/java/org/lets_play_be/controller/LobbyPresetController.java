package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyPresetControllerApi;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.ChangePresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.NewPresetRequest;
import org.lets_play_be.dto.lobbyDto.PresetFullResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
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
    public ResponseEntity<PresetFullResponse> createNewLobbyPreset(NewPresetRequest request,
                                                                   Authentication authentication) {
        PresetFullResponse response = lobbyPresetService.createNewLobbyPreset(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Deprecated
    @Override
    public ResponseEntity<List<PresetFullResponse>> getAllUserLobbyPresets(Authentication authentication) {
        List<PresetFullResponse> responseList = lobbyPresetService.getAllUserPresets(authentication);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }


    @Override
    public ResponseEntity<PresetFullResponse> getUsersLobbyPreset(long userId, Authentication auth) {
        PresetFullResponse response = lobbyPresetService.getUsersLobbyPreset(userId, auth);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PresetFullResponse> updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request,
                                                                      Authentication auth) {
        var response = lobbyPresetService.updateLobbyTitleAndTime(request, auth);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PresetFullResponse> addUsers(ChangePresetUsersRequest request) {
        var response = lobbyPresetService.addNewUsersToLobbyPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PresetFullResponse> removeUsers(ChangePresetUsersRequest request) {
        var response = lobbyPresetService.removeUserFromPreset(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StandardStringResponse> deletePreset(Long id) {
        var response = lobbyPresetService.removeLobbyPreset(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
