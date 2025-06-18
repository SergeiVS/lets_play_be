package org.lets_play_be.service.InviteService;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.InviteRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final AppUserService userService;
    private final SseNotificationService notificationService;


    public void updateIsDelivered(long inviteId) {

        Invite invite = getInviteByIdOrElseThrow(inviteId);

        invite.setDelivered(true);

        inviteRepository.save(invite);
    }

    public void updateIsSeen(Authentication auth, long inviteId) {

        var user = userService.getUserByEmailOrThrow(auth.getName());

        var invite = getInviteByIdOrElseThrow(inviteId);

        isRecipient(invite, user.getId());

        invite.setSeen(true);

        if(!invite.isDelivered()){

            invite.setDelivered(true);
        }

        inviteRepository.save(invite);
    }

    public List<InviteResponse> getAllInvitesByUser(long userId) {

        List<Invite> invites = inviteRepository.findInvitesByUserId(userId);

        isListEmpty(invites);

        invites.forEach(invite -> {

            if (!invite.isDelivered()) {
                updateIsDelivered(invite.getId());
            }
        });

        return invites.stream().map(InviteResponse::new).toList();
    }


    public List<InviteResponse> getAllInvitesByLobbyId(long lobbyId) {

        List<Invite> invites = inviteRepository.findInvitesByLobbyId(lobbyId);

        isListEmpty(invites);

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
        var lobbyId = invite.getLobby().getId();

        isLobbyOwner(invite, user.getId());

        inviteRepository.delete(invite);

        notificationService.unsubscribeUserFromSubject(user.getId(), lobbyId);

        return new InviteResponse(invite);
    }

    private void isListEmpty(List<Invite> invites) {
        if (invites.isEmpty()) {
            throw  new RestException("No invites were found", HttpStatus.NOT_FOUND);
        }
    }

    private void setNewStateToInvite(Invite invite, UpdateInviteStateRequest request) {

        String newState = request.newState();

        List<String> states = InviteState.getValuesInviteStateStringsList();

        if (!states.contains(newState.toUpperCase())) {
            throw new IllegalArgumentException("New Invite state do not meet an Enum");
        }

        if (newState.equalsIgnoreCase("delayed")) {
            validateDelayedFor(request);
            setNewStateDelayed(invite, newState, request.delayedFor());
        }

        invite.setState(InviteState.valueOf(newState.toUpperCase()));
    }

    private void isRecipient(Invite invite, long userId) {
        if (userId != invite.getRecipient().getId()) {
            throw new IllegalArgumentException("User is not recipient of this invite");
        }
    }

    private void isLobbyOwner(Invite invite, long userId) {
        if (userId != invite.getLobby().getOwner().getId()) {
            throw new RestException("User is not recipient of this invite", HttpStatus.BAD_REQUEST);
        }
    }

    private Invite getInviteByIdOrElseThrow(long inviteId) {
        return inviteRepository.findById(inviteId).orElseThrow(
                () -> new ObjectNotFoundException("Invite with id " + inviteId + " not found", Invite.class));
    }

    private void validateDelayedFor(UpdateInviteStateRequest request) {
        if (request.delayedFor() < 1) {
            throw new IllegalArgumentException("If newState equals delayed, value of delayedFor should be positive number over 0");
        }
    }

    private void setNewStateDelayed(Invite invite, String newState, int delayedFor) {
        invite.setState(InviteState.valueOf(newState.toUpperCase()));
        invite.setDelayedFor(delayedFor);
    }
}
