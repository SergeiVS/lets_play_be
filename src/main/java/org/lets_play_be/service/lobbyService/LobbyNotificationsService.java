package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
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


    public void subscribeNotifyRecipients(Lobby lobby, List<Long> recipientsIds) {
        for (long recipientId : recipientsIds) {

            if (recipientPool.isInPool(recipientId)) {
                sseNotificationService.subscribeSseObserverForActiveLobby(recipientId, lobby.getId());
            }
        }
        NotificationData notificationData = new UsersInvitedNotificationData(lobby);

        notifyInvitedUsers(lobby, notificationData);
    }

    public void unsubscribeNotifyRecipients(Lobby lobby, ChangeUsersListRequest request) {
        var lobbySubject = subjectPool.getSubject(lobby.getId());

        request.usersIds().forEach(id -> {
                    if (recipientPool.isInPool(id)) {
                        lobbySubject.unsubscribe(recipientPool.getObserver(id));
                    }
                }
        );

        final var notificationData = new UsersKickedNotificationData(lobby);
        notifyInvitedUsers(lobby, notificationData);
        notifyKickedUsers(request.usersIds(), request.message());
    }

    private void notifyKickedUsers(List<Long> userIds, String message) {
        var messageNotificationData = new MessageNotificationData(message);
        List<AppUser> users = userService.getUsersListByIds(userIds);

        users.forEach(user -> {
            if (recipientPool.isInPool(user.getId())) {
                try {
                    var userCurrentLobby = lobbyGetterService.getUserCurrentLobby(user);
                    var lobbyResponse = new LobbyResponse(userCurrentLobby);
                    var observer = recipientPool.getObserver(user.getId());

                    observer.update(createNotification(messageNotificationData));
                    observer.update(createNotification(lobbyResponse));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void notifyInvitedUsers(Lobby savedLobby, NotificationData notificationData) {
        sseNotificationService.notifyLobbyMembers(savedLobby.getId(), notificationData);
    }

    public void subscribeLobbySubjectInPool(Lobby lobby, List<Long> recipientsIds) {
        LobbySubject subject = new LobbySubject(lobby.getId());

        subjectPool.addSubject(subject);

        subscribeNotifyRecipients(lobby, recipientsIds);
    }

    public void removeLobbySubject(long lobbyId){
        subjectPool.removeSubject(lobbyId);
    }
}
