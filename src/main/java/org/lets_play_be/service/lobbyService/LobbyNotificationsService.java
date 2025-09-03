package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.dto.UsersInvitedNotificationData;
import org.lets_play_be.notification.dto.UsersKickedNotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lets_play_be.notification.NotificationFactory.createNotification;

@Service
@RequiredArgsConstructor
public class LobbyNotificationsService {

    private final LobbySubjectPool subjectPool;
    private final SseLiveRecipientPool recipientPool;
    private final SseNotificationService sseNotificationService;
    private final AppUserService userService;
    private final LobbyGetterService lobbyGetterService;


    public List<Long> subscribeNotifyRecipients(Lobby lobby, List<Long> recipientsIds) {
        List<Long> subscribedRecipients = new ArrayList<>();
        for (long recipientId : recipientsIds) {
            if (recipientPool.isInPool(recipientId)) {
                sseNotificationService.subscribeSseObserverToLobby(recipientId, lobby.getId());
                subscribedRecipients.add(recipientId);
            }
        }

        notifyInvitedUsers(
                lobby,
                lobby.getOwner().getId(),
                new UsersInvitedNotificationData(lobby)
        );

        return subscribedRecipients;
    }

    public void unsubscribeNotifyRecipients(Lobby lobby, ChangeUsersListRequest request) {
        List<AppUser> users = userService.getUsersListByIds(request.usersIds());
        var lobbySubject = subjectPool.getSubject(lobby.getId());
        var message = new MessageNotificationData(request.message());

        users.forEach(user -> {
                    if (recipientPool.isInPool(user.getId())) {
                        var observer = recipientPool.getObserver(user.getId());
                        lobbySubject.unsubscribe(observer);
                        notifyKickedUser(user, observer, message);
                    }
                }
        );

                notifyInvitedUsers(lobby, lobby.getOwner().getId(), new UsersKickedNotificationData(lobby));
    }

    public void notifyInvitedUsers(Lobby savedLobby, long userId, NotificationData notificationData) {
        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), userId, notificationData);
    }

    public List<Long> subscribeLobbySubjectInPool(Lobby lobby, List<Long> recipientsIds) {
        var subject = new LobbySubject(lobby.getId());

        subjectPool.addSubject(subject);

        return subscribeNotifyRecipients(lobby, recipientsIds);
    }

    public void removeLobbySubject(long lobbyId) {
        subjectPool.removeSubject(lobbyId);
    }

    private void notifyKickedUser(AppUser user, NotificationObserver observer, NotificationData message) {
        try {
            var userCurrentLobby = lobbyGetterService.getUserCurrentLobby(user);
            var lobbyResponse = new LobbyResponse(userCurrentLobby);

            observer.update(createNotification(message, user.getId()));
            observer.update(createNotification(lobbyResponse, user.getId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
