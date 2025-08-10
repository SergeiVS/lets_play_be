package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.LobbyControllerApi;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.service.lobbyService.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LobbyController implements LobbyControllerApi {

    private final LobbyService lobbyService;

    @Deprecated
    @Override
    public ResponseEntity<LobbyResponse> addNewLobbyActive(NewActiveLobbyRequest request,
                                                           Authentication authentication) {
        LobbyResponse response = lobbyService.createActiveLobby(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<LobbyResponse> activateLobby(ActivatePresetRequest request,
                                                       Authentication auth) {
        var response = lobbyService.createLobbyFromPreset(request, auth);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<LobbyResponse> getLobby(Authentication auth) {
        var response = lobbyService.getUsersLobby(auth);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyResponse> updateLobbyData(UpdateLobbyRequest request, Authentication auth) {
        var response = lobbyService.updateLobbyTitleAndTime(request, auth);

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<LobbyResponse> inviteNewUsers(ChangeUsersListRequest request, Authentication auth) {
        var response = lobbyService.inviteNewUsers(request, auth);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PresetFullResponse> leaveLobby(long lobbyId, Authentication auth) {
        var response = lobbyService.leaveLobby(lobbyId, auth);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyResponse> kickUsers(ChangeUsersListRequest request, Authentication auth) {
        var response = lobbyService.kickUsers(request, auth);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LobbyResponse> deactivateLobby(Long lobbyId, Authentication auth) {
        return ResponseEntity.ok(lobbyService.closeLobby(lobbyId, auth));
    }

    @Override
    public ResponseEntity<LobbyResponse> addUsers(ChangeUsersListRequest request, Authentication auth) {
        return ResponseEntity.ok(lobbyService.addUsers(request, auth));
    }

    @Override
    public ResponseEntity<LobbyResponse> removeUsers(ChangeUsersListRequest request, Authentication auth) {
        return ResponseEntity.ok(lobbyService.addUsers(request, auth));
    }

}
