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
import org.lets_play_be.notification.NotificationSubject;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.dto.Notification;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
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

import static org.lets_play_be.notification.NotificationFactory.createNotification;
import static org.lets_play_be.service.lobbyService.LobbyBaseUpdateService.setNewValues;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyActiveService {

    private final LobbyActiveRepository repository;
    private final AppUserService userService;
    private final InviteService inviteService;
    private final LobbySubjectPool subjectPool;
    private final SseLiveRecipientPool recipientPool;
    private final LobbyMappers lobbyMappers;


    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication authentication) {

        var owner = userService.getUserByEmailOrThrow(authentication.getName());

        isLobbyExistingByOwnerId(owner);

        var savedLobby = saveNewLobbyFromRequest(request, owner);

        subscribeLobbySubjectInPool(savedLobby);

        String message = "You are added to Lobby: " + savedLobby.getTitle();

        notifyRecipients(savedLobby.getId(), message);

        return lobbyMappers.toActiveResponse(savedLobby);
    }

    @Transactional
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {

        var lobbyForChange = getLobbyByIdOrThrow(request.id());

        OffsetTime newTime = FormattingUtils.timeStringToOffsetTime(request.newTime());

        setNewValues(request, lobbyForChange, newTime);

        var savedLobby = repository.save(lobbyForChange);

        return lobbyMappers.toUpdateResponse(savedLobby, savedLobby.getId());
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

    private void notifyRecipients(Long lobbyId, String message) {

        NotificationSubject subject = subjectPool.getSubject(lobbyId);

        MessageNotificationData notificationData = new MessageNotificationData(message);

        Notification notification = createNotification(notificationData);

        subject.notifyObservers(notification);

    }

    private void subscribeRecipients(LobbyActive lobby) {
        List<Long> recipientsIds = new ArrayList<>();

        recipientsIds.add(lobby.getOwner().getId());

        lobby.getInvites().forEach(invite -> recipientsIds.add(invite.getRecipient().getId()));

        NotificationSubject subject = subjectPool.getSubject(lobby.getId());

        for (Long recipientId : recipientsIds) {
            if (recipientPool.isInPool(recipientId)) {
                subject.subscribe(recipientPool.getObserver(recipientId));
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
        return inviteService.getListOfNewInvites(users, lobbyForSave, request.message());
    }

    private void isLobbyExistingByOwnerId(AppUser owner) {
        if (repository.existsById(owner.getId())) {
            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
    }
}
