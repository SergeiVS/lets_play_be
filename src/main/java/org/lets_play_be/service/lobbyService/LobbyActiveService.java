package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.dto.LobbyClosedNotificationData;
import org.lets_play_be.notification.dto.LobbyCreatedNotificationData;
import org.lets_play_be.notification.dto.Notification;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.notification.NotificationFactory.createNotification;
import static org.lets_play_be.service.lobbyService.LobbyBaseUpdateService.setNewValues;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyActiveService {

    private final LobbyActiveRepository repository;
    private final AppUserService userService;
    private final SseNotificationService sseNotificationService;
    private final InviteService inviteService;
    private final LobbySubjectPool subjectPool;
    private final LobbyMappers lobbyMappers;
    private final SseLiveRecipientPool recipientPool;

    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication authentication) {

        var owner = userService.getUserByEmailOrThrow(authentication.getName());

        isLobbyExistingByOwnerId(owner);

        var savedLobby = saveNewLobbyFromRequest(request, owner);

        subscribeLobbySubjectInPool(savedLobby);

        Notification notification = createNotification(new LobbyCreatedNotificationData(savedLobby));

        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notification);

        setInvitesDelivered(savedLobby.getInvites());

        return lobbyMappers.toActiveResponse(savedLobby);
    }

// TODO Add Ownership check.
    @Transactional
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {

        var owner = userService.getUserByEmailOrThrow(auth.getName());

        var lobbyForChange = getLobbyByIdOrThrow(request.id());

        isLobbyOwner(lobbyForChange, owner.getId());

        OffsetTime newTime = FormattingUtils.timeStringToOffsetTime(request.newTime());

        setNewValues(request, lobbyForChange, newTime);

        var savedLobby = repository.save(lobbyForChange);

        return lobbyMappers.toUpdateResponse(savedLobby, savedLobby.getId());
    }

    @Transactional
    public ActiveLobbyResponse closeLobby(Long lobbyId, Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());

        var lobbyForDelete = getLobbyByIdOrThrow(lobbyId);

        isLobbyOwner(lobbyForDelete, owner.getId());

        Notification notification = createNotification(new LobbyClosedNotificationData(lobbyForDelete));

        repository.delete(lobbyForDelete);

        sseNotificationService.notifyLobbyMembers(lobbyForDelete.getId(), notification);

        subjectPool.removeSubject(lobbyId);

        return lobbyMappers.toActiveResponse(lobbyForDelete);
    }

    public void isLobbyOwner(LobbyActive lobbyForDelete, Long id) {
        if(!Objects.equals(lobbyForDelete.getOwner().getId(), id)) {
            throw new IllegalArgumentException("User with Id: " + id + " is not owner of this lobby.");
        }
    }


    public LobbyActive getLobbyByIdOrThrow(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Lobby found with id: " + id));
    }

    private void subscribeLobbySubjectInPool(LobbyActive lobby) {

        LobbySubject subject = createLobbyNotificationSubject(lobby.getId());

        subjectPool.addSubject(subject);

        subscribeRecipients(lobby);
    }

    private LobbySubject createLobbyNotificationSubject(long lobbyId) {
        return new LobbySubject(lobbyId);
    }

    private void subscribeRecipients(LobbyActive lobby) {

        List<Long> recipientsIds = getRecipientsIds(lobby);

        for (Long recipientId : recipientsIds) {

            sseNotificationService.subscribeSseObserverForActiveLobby(recipientId, lobby.getId());
        }
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

                inviteService.updateIsDeliveredState(true, invite);
            }
        }
    }

    private LobbyActive saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {

        var title = request.title();

        OffsetTime time = timeStringToOffsetTime(request.time());

        LobbyActive lobbyForSave = new LobbyActive(title, time, owner);

        List<Invite> invitesForAdd = getSavedInviteList(request, lobbyForSave);

        lobbyForSave.getInvites().addAll(invitesForAdd);

        return repository.save(lobbyForSave);
    }

    private List<Invite> getSavedInviteList(NewActiveLobbyRequest request, LobbyActive lobbyForSave) {

        List<AppUser> users = userService.getUsersListByIds(request.userIds());

        return inviteService.createListOfNewInvites(users, lobbyForSave, request.message());
    }

    private void isLobbyExistingByOwnerId(AppUser owner) {
        if (repository.existsLobbyActiveByOwner(owner)) {
            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
    }
}
