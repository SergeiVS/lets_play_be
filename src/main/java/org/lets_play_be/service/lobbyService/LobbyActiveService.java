package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.*;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyActiveRepository;
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
public class LobbyActiveService {

    private final LobbyActiveRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final LobbyPresetService presetService;
    private final AppUserService userService;
    private final SseNotificationService sseNotificationService;
    private final InviteService inviteService;
    private final LobbySubjectPool subjectPool;
    private final SseLiveRecipientPool recipientPool;

    @Deprecated
    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication auth) {

        AppUser owner = userService.getUserByEmailOrThrow(auth.getName());

        isLobbyExistingByOwner(owner);

        LobbyActive savedLobby = saveNewLobbyFromRequest(request, owner);

        subscribeLobbySubjectInPool(savedLobby);

        var notificationData = new LobbyCreatedNotificationData(savedLobby);

        notifyInvitedUsers(savedLobby, notificationData);

        setInvitesDelivered(savedLobby.getInvites());

        return new ActiveLobbyResponse(savedLobby);
    }

    @Transactional
    public ActiveLobbyResponse createLobbyFromPreset(ActivatePresetRequest request, Authentication auth) {

        var owner = userService.getUserByEmailOrThrow(auth.getName());

        isLobbyExistingByOwner(owner);

        var preset = presetService.getPresetByOwnerIdOrThrow(owner.getId());
        var lobby = saveNewLobbyFromPreset(request, preset);

        subscribeLobbySubjectInPool(lobby);

        var notificationData = new LobbyCreatedNotificationData(lobby);

        notifyInvitedUsers(lobby, notificationData);

        setInvitesDelivered(lobby.getInvites());

        return new ActiveLobbyResponse(lobby);
    }

    public ActiveLobbyResponse getUsersActiveLobby(Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());

        Optional<LobbyActive> optionalLobby = repository.findLobbyActiveByOwnerId(owner.getId());

        return optionalLobby.map(ActiveLobbyResponse::new).orElse(null);
    }

    @Transactional
    public ActiveLobbyResponse inviteNewUsers(InviteOrKickUsersRequest request, Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());
        var lobby = loadLobbyByOwnerIdOrThrow(owner);

        List<Invite> newInvites = getNewInvitesList(request.usersIds(), request.message(), lobby);
        lobby.getInvites().addAll(newInvites);

        var updatedLobby = repository.save(lobby);

        var savedNewInvites = updatedLobby.getInvites().stream()
                .filter(invite -> request.usersIds().contains(invite.getRecipient().getId()))
                .toList();

        NotificationData notificationData = new UsersInvitedNotificationData(updatedLobby);

        subscribeRecipients(updatedLobby.getId(), request.usersIds());

        notifyInvitedUsers(updatedLobby, notificationData);

        setInvitesDelivered(savedNewInvites);

        return new ActiveLobbyResponse(updatedLobby);
    }

    @Transactional
    public PresetFullResponse leaveLobby(long lobbyId, Authentication auth) {
        var user = userService.getUserByEmailOrThrow(auth.getName());
        var lobby = getLobbyByIdOrThrow(lobbyId);

        isInLobby(lobby, user);

        lobby.getInvites().removeIf(invite -> invite.getRecipient().getId().equals(user.getId()));
        var updatedLobby = repository.save(lobby);

        var message = user.getName() + " was leaved the lobby: " + updatedLobby.getTitle();

        notifyInvitedUsers(updatedLobby, new MessageNotificationData(message));

        return presetService.getPresetFullResponse(user);
    }

    @Transactional
    public ActiveLobbyResponse kickUsers(InviteOrKickUsersRequest request, Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());
        var lobby = loadLobbyByOwnerIdOrThrow(owner);
        List<Long> kickedUsersIds = request.usersIds();

        lobby.getInvites().removeIf(invite -> kickedUsersIds.contains(invite.getRecipient().getId()));
        var updatedLobby = repository.save(lobby);

        unsubscribeRecipients(updatedLobby.getId(), kickedUsersIds);

        var lobbyMembersNotificationData = new UsersKickedNotificationData(updatedLobby);

        notifyInvitedUsers(updatedLobby, lobbyMembersNotificationData);

        notifyKickedUsers(kickedUsersIds, request.message());

        return new ActiveLobbyResponse(updatedLobby);
    }

    @Transactional
    public ActiveLobbyResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {
        AppUser owner = userService.getUserByEmailOrThrow(auth.getName());

        LobbyActive lobbyForChange = getLobbyByIdOrThrow(request.lobbyId());

        baseUpdateService.setNewValues(request, lobbyForChange, owner.getId());

        LobbyActive savedLobby = repository.save(lobbyForChange);

        var notificationData = new LobbyUpdatedNotificationData(savedLobby);

        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);

        return new ActiveLobbyResponse(savedLobby);
    }

    @Transactional
    public ActiveLobbyResponse closeLobby(Long lobbyId, Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());

        var lobbyForDelete = getLobbyByIdOrThrow(lobbyId);

        baseUpdateService.isLobbyOwner(lobbyForDelete, owner.getId());

        repository.delete(lobbyForDelete);

        var data = new LobbyClosedNotificationData(lobbyForDelete);

        sseNotificationService.notifyLobbyMembers(lobbyForDelete.getId(), data);

        subjectPool.removeSubject(lobbyId);

        return new ActiveLobbyResponse(lobbyForDelete);
    }


    public LobbyActive loadLobbyByOwnerIdOrThrow(AppUser owner) {
        return repository.findLobbyActiveByOwnerId(owner.getId())
                .orElseThrow(() -> new RestException("Current user does not have active lobby", HttpStatus.BAD_REQUEST));
    }

    public LobbyActive getLobbyByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Lobby found with lobbyId: " + id));
    }

    private void subscribeLobbySubjectInPool(LobbyActive lobby) {
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

        recipientsIds.forEach(id-> {
                    if (recipientPool.isInPool(id)){
                        lobbySubject.unsubscribe(recipientPool.getObserver(id));
                    }
                }
        );
    }

    private List<Long> getRecipientsIds(LobbyActive lobby) {
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

    private LobbyActive saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {
        var title = request.title();
        var time = timeStringToOffsetTime(request.time());
        var lobbyForSave = new LobbyActive(title, time, owner);

        List<Invite> newInvitesList = getNewInvitesList(request.userIds(), request.message(), lobbyForSave);

        lobbyForSave.getInvites().addAll(newInvitesList);

        return repository.save(lobbyForSave);
    }

    private LobbyActive saveNewLobbyFromPreset(ActivatePresetRequest request, LobbyPreset preset) {
        var lobby = new LobbyActive(preset);
        var userIds = preset.getUsers().stream().map(AppUser::getId).toList();

        List<Invite> invites = getNewInvitesList(userIds, request.message(), lobby);
        lobby.getInvites().addAll(invites);

        return repository.save(lobby);
    }

    private List<Invite> getNewInvitesList(List<Long> usersId, String message, LobbyActive lobbyForSave) {
        List<AppUser> users = userService.getUsersListByIds(usersId);

        return users.stream().map(user -> new Invite(user, lobbyForSave, message)).toList();
    }

    private void notifyInvitedUsers(LobbyActive savedLobby, NotificationData notificationData) {
        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);
    }

    private void notifyKickedUsers(List<Long> userIds, String message){
        var messageNotificationData= new MessageNotificationData(message);
        List<AppUser> users = userService.getUsersListByIds(userIds);

        users.forEach(user->{
            if(recipientPool.isInPool(user.getId())){
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

    private void isInLobby(LobbyActive lobby, AppUser user) {
        var inviteOpt = lobby.getInvites().stream()
                .filter(invite -> invite.getRecipient().getId().equals(user.getId()))
                .findFirst();

        if(inviteOpt.isEmpty()){
            throw new IllegalArgumentException("User is not a lobby member");
        }
    }
}
