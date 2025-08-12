package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActivatePresetRequest;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyRequest;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.LobbyActivatedNotificationData;
import org.lets_play_be.notification.dto.LobbyClosedNotificationData;
import org.lets_play_be.notification.dto.LobbyUpdatedNotificationData;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyService {

    public static final String LEFT_LOBBY_MSG = "%s has left the lobby: %s";
    private final LobbyRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final LobbyUserService lobbyUserService;
    private final LobbyGetterService lobbyGetter;
    private final LobbyNotificationsService lobbyNotificationsService;
    private final AppUserService userService;
    private final InviteService inviteService;
    private final SseLiveRecipientPool recipientPool;
    private final SseNotificationService sseNotificationService;

    public LobbyResponse getUserLobby(Authentication auth) {
        var user = userService.getUserByEmailOrThrow(auth.getName());

        Lobby lobby = lobbyGetter.getUserCurrentLobby(user);

        return new LobbyResponse(lobby);
    }

    @Transactional
    public LobbyResponse activateLobby(ActivatePresetRequest request, Authentication auth) {
        var user = userService.getUserByEmailOrThrow(auth.getName());

        Lobby lobby = lobbyGetter.findOrCreateUserLobby(user);
        lobby.setType(LobbyType.ACTIVE);
        restoreInitialInvitesState(lobby, request.message());
        Lobby activatedLobby = repository.save(lobby);

        var notificationData = new LobbyActivatedNotificationData(lobby);
        lobbyNotificationsService.subscribeLobbySubjectInPool(lobby, getRecipientsIds(lobby));
        lobbyNotificationsService.notifyInvitedUsers(lobby, notificationData);

        setInvitesDelivered(lobby.getInvites());

        return new LobbyResponse(activatedLobby);
    }

    public LobbyResponse inviteNewUsers(ChangeUsersListRequest request, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't invite users to an inactive Lobby", HttpStatus.BAD_REQUEST);
        }

        var updatedLobby = lobbyUserService.addUsers(request, auth);
        var savedNewInvites = updatedLobby.getInvites().stream()
                .filter(invite -> request.usersIds().contains(invite.getRecipient().getId()))
                .toList();

        lobbyNotificationsService.subscribeNotifyRecipients(updatedLobby, request.usersIds());

        setInvitesDelivered(savedNewInvites);

        return new LobbyResponse(updatedLobby);
    }

    public LobbyResponse leaveLobby(long lobbyId, Authentication auth) {
        var user = userService.getUserByEmailOrThrow(auth.getName());
        var lobby = lobbyGetter.getLobbyByIdOrThrow(lobbyId);

        if (!isActive(lobby)) {
            throw new RestException("You can't leave an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        isInLobby(lobby, user);

        lobby.getInvites().removeIf(invite -> invite.getRecipient().getId().equals(user.getId()));

        var updatedLobby = repository.save(lobby);

        lobbyNotificationsService.notifyInvitedUsers(
                updatedLobby,
                new MessageNotificationData(
                        LEFT_LOBBY_MSG
                                .formatted(
                                        user.getName(),
                                        updatedLobby.getTitle()
                                )
                )
        );

        return new LobbyResponse(lobbyGetter.getUserCurrentLobby(user));
    }

    @Transactional
    public LobbyResponse kickUsers(ChangeUsersListRequest request, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't kick users from an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        final var updatedLobby = lobbyUserService.removeUsers(request, auth);

        lobbyNotificationsService.unsubscribeNotifyRecipients(updatedLobby, request);

        return new LobbyResponse(updatedLobby);
    }

    @Transactional
    public LobbyResponse removeUsers(ChangeUsersListRequest request, Authentication auth) {
        var lobby = lobbyGetter.loadLobbyByAuth(auth);
        if (isActive(lobby)) {
            throw new RestException("You can't remove users from an active lobby (kicks only)",
                    HttpStatus.BAD_REQUEST);
        }

        return new LobbyResponse(lobbyUserService.removeUsers(request, auth));
    }

    @Transactional
    public LobbyResponse addUsers(ChangeUsersListRequest request, Authentication auth) {
        var lobby = lobbyGetter.loadLobbyByAuth(auth);
        if (isActive(lobby)) {
            throw new RestException("You can't add users to an active lobby (invites only)", HttpStatus.BAD_REQUEST);
        }

        return new LobbyResponse(lobbyUserService.addUsers(request, auth));
    }


    @Transactional
    public LobbyResponse updateLobbyTitleAndTime(UpdateLobbyRequest request, Authentication auth) {
        AppUser owner = userService.getUserByEmailOrThrow(auth.getName());

        Lobby lobbyForChange = lobbyGetter.getLobbyByIdOrThrow(request.lobbyId());

        baseUpdateService.setNewValues(request, lobbyForChange, owner.getId());

        Lobby savedLobby = repository.save(lobbyForChange);

        if (isActive(savedLobby)) {
            var notificationData = new LobbyUpdatedNotificationData(savedLobby);

            sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);
        }

        return new LobbyResponse(savedLobby);
    }

    @Transactional
    public LobbyResponse deActivateLobby(Long lobbyId, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't close an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        var owner = userService.getUserByEmailOrThrow(auth.getName());
        var lobbyForDeactivate = lobbyGetter.getLobbyByIdOrThrow(lobbyId);

        baseUpdateService.isLobbyOwner(lobbyForDeactivate, owner.getId());

        var savedLobby = saveDeactivatedLobby(lobbyForDeactivate);

        var data = new LobbyClosedNotificationData(savedLobby);
        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), data);
        lobbyNotificationsService.removeLobbySubject(savedLobby.getId());

        return new LobbyResponse(savedLobby);
    }

    private Lobby saveDeactivatedLobby(Lobby lobbyForDeactivate) {
        lobbyForDeactivate.setType(LobbyType.INACTIVE);
        lobbyForDeactivate.getInvites()
                .forEach(invite -> invite.setState(InviteState.INACTIVE));
        return repository.save(lobbyForDeactivate);
    }

    private void restoreInitialInvitesState(Lobby lobby, String message) {
        if (!lobby.getInvites().isEmpty()) {
            lobby.getInvites().forEach(invite -> {
                invite.setState(InviteState.PENDING);
                invite.setDelivered(false);
                invite.setSeen(false);
                invite.setMessage(message);
            });
        }
    }

    private List<Long> getRecipientsIds(Lobby lobby) {
        List<Long> recipientsIds = new ArrayList<>();

        lobby.getInvites().forEach(invite -> recipientsIds.add(invite.getRecipient().getId()));

        return recipientsIds;
    }

    private void setInvitesDelivered(List<Invite> invites) {
        for (Invite invite : invites) {

            var recipientId = invite.getRecipient().getId();

            if (recipientPool.isInPool(recipientId)) {

                inviteService.updateIsDelivered(invite.getId());
            }
        }

        inviteService.saveInvitesList(invites);
    }

    private void isInLobby(Lobby lobby, AppUser user) {
        var inviteOpt = lobby.getInvites().stream()
                .filter(invite -> invite.getRecipient().getId().equals(user.getId()))
                .findFirst();

        if (inviteOpt.isEmpty()) {
            throw new IllegalArgumentException("User is not a lobby member");
        }
    }

    private boolean isActive(Lobby lobby) {
        return lobby.getType() == LobbyType.ACTIVE;
    }
}
