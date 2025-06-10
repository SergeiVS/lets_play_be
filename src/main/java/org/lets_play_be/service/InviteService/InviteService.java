package org.lets_play_be.service.InviteService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.InviteRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final AppUserService userService;
    private final SseNotificationService notificationService;

    public List<Invite> createListOfNewInvites(List<AppUser> users, LobbyActive lobby, String message) {

        return users.stream().map(user -> new Invite(user, lobby, message)).toList();
    }

    public void updateIsDeliveredState(boolean isDelivered, Invite invite) {

        invite.setDelivered(isDelivered);

        inviteRepository.save(invite);
    }

    public void updateIsSeen(Authentication auth, long inviteId) {
        var user = userService.getUserByEmailOrThrow(auth.getName());

        var invite = getInviteByIdOrElseThrow(inviteId);

        if (Objects.equals(user.getId(), invite.getRecipient().getId())) {

            invite.setSeen(true);

            inviteRepository.save(invite);
        } else {
            throw new RestException("Authenticated user is not Invite recipient", HttpStatus.BAD_REQUEST);
        }
    }

    public List<Invite> getNotDeliveredInvitesByUserId(long userId) {
        return inviteRepository.findNotDeliveredInvitesByUserId(userId);
    }


    public List<InviteResponse> getAllInvitesByUser(long userId) {
        List<Invite> invites = inviteRepository.findInvitesByUserId(userId);
        return invites.stream().map(InviteResponse::new).toList();
    }


    public List<InviteResponse> getAllInvitesByLobbyId(long lobbyId) {

        List<Invite> invites = inviteRepository.findInvitesByLobbyId(lobbyId);
        return invites.stream().map(InviteResponse::new).toList();
    }

    public InviteResponse updateInviteState(UpdateInviteStateRequest request) {

        var invite = getInviteByIdOrElseThrow(request.inviteId());

        isRecipient(invite, request.userId());

        setNewStateToInvite(invite, request);

        var savedInvite = inviteRepository.save(invite);

        var response = new InviteResponse(savedInvite);

        notificationService.notifyLobbyMembers(savedInvite.getLobby().getId(), response);

        return response;
    }

    public InviteResponse removeInvite(long inviteId, Authentication auth) {

        var invite = getInviteByIdOrElseThrow(inviteId);
        var user = userService.getUserByEmailOrThrow(auth.getName());
        isLobbyOwner(invite, user.getId());
        inviteRepository.delete(invite);

        return new InviteResponse(invite);
    }

    private void setNewStateToInvite(Invite invite, UpdateInviteStateRequest request) {

        String newState = request.newState();

        List<String> states = InviteState.getValuesInviteStateStringsList();

        if (newState.equalsIgnoreCase("delayed")) {

            validateDelayedFor(request);
            invite.setState(InviteState.valueOf(newState.toUpperCase()));
            invite.setDelayedFor(request.delayedFor());

        } else if (states.contains(newState.toUpperCase())) {

            invite.setState(InviteState.valueOf(newState.toUpperCase()));
        } else {

            throw new IllegalArgumentException("New Invite state do not meet an Enum");
        }
    }

    private static void validateDelayedFor(UpdateInviteStateRequest request) {
        if (request.delayedFor() < 1) {
            throw new IllegalArgumentException("By newState== delayed, value of delayedFor should be positive number");
        }
    }

    private void isRecipient(Invite invite, long userId) {
        if (userId != invite.getRecipient().getId()) {
            throw new RestException("User is not recipient of this invite", HttpStatus.BAD_REQUEST);
        }
    }

    private void isLobbyOwner(Invite invite, long userId) {
        if (userId != invite.getLobby().getOwner().getId()) {
            throw new RestException("User is not recipient of this invite", HttpStatus.BAD_REQUEST);
        }
    }

    private Invite getInviteByIdOrElseThrow(long inviteId) {
        return inviteRepository.findById(inviteId).orElseThrow(
                () -> new RestException("Invite not found", HttpStatus.BAD_REQUEST));
    }

}
