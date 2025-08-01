package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.InviteControllerApi;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.service.InviteService.InviteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InviteController implements InviteControllerApi {

    private final InviteService inviteService;

    @Override
    public ResponseEntity<List<InviteResponse>> getAllUserInvitesByUser(long userId) {

        List<InviteResponse> response = inviteService.getAllInvitesByUser(userId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<InviteResponse>> getAllInvitesByLobby(long lobbyId) {

        List<InviteResponse> response = inviteService.getAllInvitesByLobbyId(lobbyId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InviteResponse> deleteInvite(UpdateInviteStateRequest request) {

        var response = inviteService.updateInviteState(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StandardStringResponse> updateInviteISeen(long inviteId, Authentication auth) {

        inviteService.updateIsSeen(auth, inviteId);
        var response = new StandardStringResponse("Is seen was updated to " + inviteId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InviteResponse> deleteInvite(long inviteId, Authentication auth) {

        var response = inviteService.removeInvite(inviteId, auth);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
