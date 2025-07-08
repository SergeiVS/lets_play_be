package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.dto.LobbyClosedNotificationData;
import org.lets_play_be.notification.dto.LobbyCreatedNotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyActiveService {

    private final LobbyActiveRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final AppUserService userService;
    private final SseNotificationService sseNotificationService;
    private final InviteService inviteService;
    private final LobbySubjectPool subjectPool;
    private final SseLiveRecipientPool recipientPool;

    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication authentication) {

        AppUser owner = userService.getUserByEmailOrThrow(authentication.getName());

        isLobbyExistingByOwnerId(owner);

        LobbyActive savedLobby = saveNewLobbyFromRequest(request, owner);

        subscribeLobbySubjectInPool(savedLobby);

        var notificationData = new LobbyCreatedNotificationData(savedLobby);

        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);

        setInvitesDelivered(savedLobby.getInvites());

        return new ActiveLobbyResponse(savedLobby);
    }

    @Transactional
    public ActiveLobbyResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {

        AppUser owner = userService.getUserByEmailOrThrow(auth.getName());

        LobbyActive lobbyForChange = getLobbyByIdOrThrow(request.id());

        baseUpdateService.setNewValues(request, lobbyForChange, owner.getId());

        LobbyActive savedLobby = repository.save(lobbyForChange);

        return new ActiveLobbyResponse(savedLobby);
    }

    @Transactional
    public ActiveLobbyResponse closeLobby(Long lobbyId, Authentication auth) {

        var owner = userService.getUserByEmailOrThrow(auth.getName());

        var lobbyForDelete = getLobbyByIdOrThrow(lobbyId);

        baseUpdateService.isLobbyOwner(lobbyForDelete, owner.getId());

        var data = new LobbyClosedNotificationData(lobbyForDelete);

        repository.delete(lobbyForDelete);

        sseNotificationService.notifyLobbyMembers(lobbyForDelete.getId(), data);

        subjectPool.removeSubject(lobbyId);

        return new ActiveLobbyResponse(lobbyForDelete);
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

        for (long recipientId : recipientsIds) {

            if(recipientPool.isInPool(recipientId)) {
            sseNotificationService.subscribeSseObserverForActiveLobby(recipientId, lobby.getId());
            }
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

                inviteService.updateIsDelivered(invite.getId());
            }
        }
    }

    private LobbyActive saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {

        var title = request.title();

        OffsetTime time = timeStringToOffsetTime(request.time());

        LobbyActive lobbyForSave = new LobbyActive(title, time, owner);

        List<Invite> newInvitesList = getNewInvitesList(request, lobbyForSave);

        lobbyForSave.getInvites().addAll(newInvitesList);

        return repository.save(lobbyForSave);
    }

    private List<Invite> getNewInvitesList(NewActiveLobbyRequest request, LobbyActive lobbyForSave) {

        List<AppUser> users = userService.getUsersListByIds(request.userIds());

        return users.stream().map(user -> new Invite(user, lobbyForSave, request.message())).toList();
    }

    private void isLobbyExistingByOwnerId(AppUser owner) {

        if (repository.existsLobbyActiveByOwner(owner)) {

            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
    }
}
