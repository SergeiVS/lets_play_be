package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.*;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lets_play_be.notification.NotificationFactory.createNotification;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyService {

    public static final String LEFT_LOBBY_MSG = "%s has left the lobby: %s";
    private final LobbyRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final LobbyPresetService presetService;
    private final AppUserService userService;
    private final SseNotificationService sseNotificationService;
    private final InviteService inviteService;
    private final LobbySubjectPool subjectPool;
    private final SseLiveRecipientPool recipientPool;
    private final LobbyUserService lobbyUserService;
    private final LobbyGetterService lobbyGetter;

    @Deprecated
    @Transactional
    public LobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication auth) {
        //TODO delete
        AppUser owner = userService.getUserByEmailOrThrow(auth.getName());

        isLobbyExistingByOwner(owner);

        Lobby savedLobby = saveNewLobbyFromRequest(request, owner);

        subscribeLobbySubjectInPool(savedLobby);

        var notificationData = new LobbyCreatedNotificationData(savedLobby);

        notifyInvitedUsers(savedLobby, notificationData);

        setInvitesDelivered(savedLobby.getInvites());

        return new LobbyResponse(savedLobby);
    }

    @Transactional
    public LobbyResponse createLobbyFromPreset(ActivatePresetRequest request, Authentication auth) {
        //TODO rework to not use presets
        var owner = userService.getUserByEmailOrThrow(auth.getName());

        isLobbyExistingByOwner(owner);

        var preset = presetService.getPresetByOwnerIdOrThrow(owner.getId());
        var lobby = saveNewLobbyFromPreset(request, preset);

        subscribeLobbySubjectInPool(lobby);

        var notificationData = new LobbyCreatedNotificationData(lobby);

        notifyInvitedUsers(lobby, notificationData);

        setInvitesDelivered(lobby.getInvites());

        return new LobbyResponse(lobby);
    }

    public LobbyResponse getUsersLobby(Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());

        Optional<Lobby> optionalLobby = repository.findLobbyByOwnerId(owner.getId());

        return optionalLobby.map(LobbyResponse::new).orElse(null);
    }

    public LobbyResponse inviteNewUsers(ChangeUsersListRequest request, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't invite users to an inactive Lobby", HttpStatus.BAD_REQUEST);
        }

        var updatedLobby = lobbyUserService.addUsers(request, auth);
        //TODO generify invite sequence
        var savedNewInvites = updatedLobby.getInvites().stream()
                .filter(invite -> request.usersIds().contains(invite.getRecipient().getId()))
                .toList();

        NotificationData notificationData = new UsersInvitedNotificationData(updatedLobby);

        subscribeRecipients(updatedLobby.getId(), request.usersIds());

        notifyInvitedUsers(updatedLobby, notificationData);

        setInvitesDelivered(savedNewInvites);

        return new LobbyResponse(updatedLobby);
    }

    public PresetFullResponse leaveLobby(long lobbyId, Authentication auth) {
        var user = userService.getUserByEmailOrThrow(auth.getName());
        var lobby = lobbyGetter.getLobbyByIdOrThrow(lobbyId);

        if (!isActive(lobby)) {
            throw new RestException("You can't leave an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        isInLobby(lobby, user);

        lobby.getInvites().removeIf(invite -> invite.getRecipient().getId().equals(user.getId()));

        var updatedLobby = repository.save(lobby);

        notifyInvitedUsers(
                updatedLobby,
                new MessageNotificationData(
                        LEFT_LOBBY_MSG
                                .formatted(
                                        user.getName(),
                                        updatedLobby.getTitle()
                                )
                )
        );

        return presetService.getPresetFullResponse(user);
    }

    @Transactional
    public LobbyResponse kickUsers(ChangeUsersListRequest request, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't kick users from an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        final var updatedLobby = lobbyUserService.removeUsers(request, auth);

        //TODO generify invite sequence
        final var lobbyMembersNotificationData = new UsersKickedNotificationData(updatedLobby);

        unsubscribeRecipients(updatedLobby.getId(), request.usersIds());

        notifyInvitedUsers(updatedLobby, lobbyMembersNotificationData);

        notifyKickedUsers(request.usersIds(), request.message());

        return new LobbyResponse(updatedLobby);
    }

    public LobbyResponse removeUsers(ChangeUsersListRequest request, Authentication auth) {
        var lobby = lobbyGetter.loadLobbyByAuth(auth);
        if (isActive(lobby)) {
            throw new RestException("You can't remove users from an active lobby (kicks only)",
                                    HttpStatus.BAD_REQUEST);
        }

        return new LobbyResponse(lobbyUserService.removeUsers(request, auth));
    }

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
    public LobbyResponse closeLobby(Long lobbyId, Authentication auth) {
        if (!isActive(lobbyGetter.loadLobbyByAuth(auth))) {
            throw new RestException("You can't close an inactive lobby", HttpStatus.BAD_REQUEST);
        }

        var owner = userService.getUserByEmailOrThrow(auth.getName());

        var lobbyForDelete = lobbyGetter.getLobbyByIdOrThrow(lobbyId);

        baseUpdateService.isLobbyOwner(lobbyForDelete, owner.getId());

        repository.delete(lobbyForDelete);

        var data = new LobbyClosedNotificationData(lobbyForDelete);

        sseNotificationService.notifyLobbyMembers(lobbyForDelete.getId(), data);

        subjectPool.removeSubject(lobbyId);

        return new LobbyResponse(lobbyForDelete);
    }

    private void subscribeLobbySubjectInPool(Lobby lobby) {
        LobbySubject subject = createLobbyNotificationSubject(lobby.getId());

        subjectPool.addSubject(subject);

        List<Long> recipientsIds = getRecipientsIds(lobby);

        subscribeRecipients(lobby.getId(), recipientsIds);
    }

    private LobbySubject createLobbyNotificationSubject(long lobbyId) {
        return new LobbySubject(lobbyId);
    }

    private void subscribeRecipients(long lobbyId, List<Long> recipientsIds) {
        for (long recipientId : recipientsIds) {

            if (recipientPool.isInPool(recipientId)) {
                sseNotificationService.subscribeSseObserverForActiveLobby(recipientId, lobbyId);
            }
        }
    }

    private void unsubscribeRecipients(long lobbyId, List<Long> recipientsIds) {
        var lobbySubject = subjectPool.getSubject(lobbyId);

        recipientsIds.forEach(id -> {
                                  if (recipientPool.isInPool(id)) {
                                      lobbySubject.unsubscribe(recipientPool.getObserver(id));
                                  }
                              }
        );
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
    }

    private Lobby saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {
        var title = request.title();
        var time = timeStringToOffsetTime(request.time());
        var lobbyForSave = new Lobby(title, time, owner);

        List<Invite> newInvitesList = getNewInvitesList(request.userIds(), request.message(), lobbyForSave);

        lobbyForSave.getInvites().addAll(newInvitesList);

        return repository.save(lobbyForSave);
    }

    private Lobby saveNewLobbyFromPreset(ActivatePresetRequest request, LobbyPreset preset) {
        var lobby = new Lobby(preset);
        var userIds = preset.getUsers().stream().map(AppUser::getId).toList();

        List<Invite> invites = getNewInvitesList(userIds, request.message(), lobby);
        lobby.getInvites().addAll(invites);

        return repository.save(lobby);
    }

    private List<Invite> getNewInvitesList(List<Long> usersId, String message, Lobby lobbyForSave) {
        List<AppUser> users = userService.getUsersListByIds(usersId);

        return users.stream().map(user -> new Invite(user, lobbyForSave, message)).toList();
    }

    private void notifyInvitedUsers(Lobby savedLobby, NotificationData notificationData) {
        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);
    }

    private void notifyKickedUsers(List<Long> userIds, String message) {
        var messageNotificationData = new MessageNotificationData(message);
        List<AppUser> users = userService.getUsersListByIds(userIds);

        users.forEach(user -> {
            if (recipientPool.isInPool(user.getId())) {
                try {
                    var presetResponse = presetService.getPresetFullResponse(user);
                    var observer = recipientPool.getObserver(user.getId());

                    observer.update(createNotification(messageNotificationData));
                    observer.update(createNotification(presetResponse));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void isLobbyExistingByOwner(AppUser owner) {
        if (repository.existsLobbyActiveByOwner(owner)) {

            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
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
