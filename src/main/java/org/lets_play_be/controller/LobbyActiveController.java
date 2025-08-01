package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyActiveControllerApi;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.service.lobbyService.LobbyActiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LobbyActiveController implements LobbyActiveControllerApi {

    private final LobbyActiveService lobbyService;

    @Deprecated
    @Override
    public ResponseEntity<ActiveLobbyResponse> addNewLobbyActive(NewActiveLobbyRequest request,
                                                                 Authentication authentication) {
        ActiveLobbyResponse response = lobbyService.createActiveLobby(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<ActiveLobbyResponse> createActiveLobby(ActivatePresetRequest request,
                                                                 Authentication auth) {
        var response = lobbyService.createLobbyFromPreset(request, auth);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<ActiveLobbyResponse> getUsersActiveLobby(Authentication auth) {

        var response = lobbyService.getUsersActiveLobby(auth);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ActiveLobbyResponse> updateLobbyActiveTitleAndTile(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {
        var response = lobbyService.updateLobbyTitleAndTime(request, auth);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<ActiveLobbyResponse> inviteNewUsers(InviteNewUsersRequest request, Authentication auth) {

        var response = lobbyService.inviteNewUsers(request, auth);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ActiveLobbyResponse> deleteActiveLobby(Long lobbyId, Authentication auth) {
        return ResponseEntity.ok(lobbyService.closeLobby(lobbyId, auth));
    }

}
