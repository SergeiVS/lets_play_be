package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.InviteControllerApi;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InviteController implements InviteControllerApi {

    private final InviteService inviteService;
    private final AppUserService appUserService;

    @Override
    public ResponseEntity<List<InviteResponse>> getAllInvitesForAUser(Authentication auth) {
        List<InviteResponse> response =
                inviteService.getAllUserInviteResponses(
                        appUserService.getUserByEmailOrThrow(auth.getName()).getId()
                );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<InviteResponse>> getAllInvitesByLobby(long lobbyId) {
        List<InviteResponse> response = inviteService.getAllInvitesByLobbyId(lobbyId);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<InviteResponse> updateInvite(UpdateInviteStateRequest request, Authentication auth) {
        var user = appUserService.getUserByEmailOrThrow(auth.getName());

        var response = inviteService.updateInviteState(request, user.getId());

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
