package org.lets_play_be.service.InviteService;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.invite.Invite;
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

    private final InviteRepository repository;
    private final AppUserService userService;
    private final SseNotificationService notificationService;


    public void updateIsDelivered(long inviteId) {

        Invite invite = getInviteByIdOrElseThrow(inviteId);

        invite.setDelivered(true);

        repository.save(invite);
    }

    public void updateIsSeen(Authentication auth, long inviteId) {

        var user = userService.getUserByEmailOrThrow(auth.getName());

        var invite = getInviteByIdOrElseThrow(inviteId);

        isRecipientGuard(invite, user.getId());

        invite.setSeen(true);

        if (!invite.isDelivered()) {

            invite.setDelivered(true);
        }

        repository.save(invite);
    }

    public List<Invite> findAllUsersInvites(long userId) {
        return repository.findInvitesByUserId(userId);
    }

    public List<InviteResponse> getAllUserInviteResponses(long userId) {

        List<Invite> invites = findAllUsersInvites(userId);

        isListEmpty(invites);

        invites.forEach(invite -> {
            if (!invite.isDelivered()) {
                updateIsDelivered(invite.getId());
            }
        });

        return invites.stream().map(InviteResponse::new).toList();
    }


    public List<InviteResponse> getAllInvitesByLobbyId(long lobbyId) {

        List<Invite> invites = repository.findInvitesByLobbyId(lobbyId);

        isListEmpty(invites);

        return invites.stream().map(InviteResponse::new).toList();
    }

    public InviteResponse updateInviteState(UpdateInviteStateRequest request, long userId) {
        var invite = getInviteByIdOrElseThrow(request.inviteId());

        isRecipientGuard(invite, userId);

        setNewStateToInvite(invite, request);

        var savedInvite = repository.save(invite);

        var response = new InviteResponse(savedInvite);

        notificationService.notifyLobbyMembers(savedInvite.getLobby().getId(), userId, response);

        return response;
    }

    public InviteResponse removeInvite(long inviteId, Authentication auth) {

        var invite = getInviteByIdOrElseThrow(inviteId);
        var user = userService.getUserByEmailOrThrow(auth.getName());
        var lobbyId = invite.getLobby().getId();

        isLobbyOwner(invite, user.getId());

        repository.delete(invite);

        notificationService.unsubscribeUserFromSubject(user.getId(), lobbyId);

        return new InviteResponse(invite);
    }

    public void setInvitesDelivered(List<Invite> invites, List<Long> subscribedRecipientsIds) {
        if (subscribedRecipientsIds != null && !subscribedRecipientsIds.isEmpty()) {
            for (Invite invite : invites) {
                var recipientId = invite.getRecipient().getId();

                if (subscribedRecipientsIds.contains(recipientId)) {
                    invite.setDelivered(true);
                }
            }
            repository.saveAll(invites);
        }
    }

    private void isListEmpty(List<Invite> invites) {
        if (invites.isEmpty()) {
            throw new RestException("No invites were found", HttpStatus.NOT_FOUND);
        }
    }

    private void setNewStateToInvite(Invite invite, UpdateInviteStateRequest request) {
        if (request.newState() == InviteState.DELAYED) {
            validateDelayedFor(request);

            invite.setState(request.newState());
            invite.setDelayedFor(request.delayedFor());
        } else {
            invite.setState(request.newState());
        }
    }

    private void isRecipientGuard(Invite invite, long userId) {
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
        return repository.findById(inviteId).orElseThrow(
                () -> new ObjectNotFoundException("invite with lobbyId " + inviteId + " not found", Invite.class));
    }

    private void validateDelayedFor(UpdateInviteStateRequest request) {
        if (request.delayedFor() < 1) {
            throw new IllegalArgumentException("If newState equals delayed, value of delayedFor should be positive number over 0");
        }
    }
}
