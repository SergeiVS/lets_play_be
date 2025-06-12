package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.dto.LobbyCreatedNotificationData;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static org.lets_play_be.notification.NotificationFactory.createNotification;

@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseService sseService;

    private final SseLiveRecipientPool recipientPool;

    private final LobbySubjectPool subjectPool;

    private final AppUserService userService;

    private final InviteService inviteService;


    public SseEmitter subscribeForSse(Authentication auth) {

        var recipientId = userService.getUserIdByEmailOrThrow(auth.getName());

        final SseEmitter emitter = sseService.createSseConnection();

        createSseObserver(emitter, recipientId);

        return emitter;
    }


    public void subscribeSseObserverForActiveLobby(long recipientId, long lobbyId) {

        try {
            if (recipientPool.isInPool(recipientId)) {

                SseNotificationObserver observer = ((SseNotificationObserver) recipientPool.getObserver(recipientId));

                LobbySubject subject = ((LobbySubject) subjectPool.getSubject(lobbyId));

                observer.addOnCloseCallback(lobbyId, subject.removeObserver(observer));

                subject.subscribe(observer);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyLobbyMembers(long lobbyId, NotificationData data) {

        var subject = subjectPool.getSubject(lobbyId);

        var notification = createNotification(data);

        subject.notifyObservers(notification);
    }

    public void sendMissedNotifications(Authentication authentication) {

        var recipientId = userService.getUserIdByEmailOrThrow(authentication.getName());

        try {
            List<Invite> invites = inviteService.getNotDeliveredInvitesByUserId(recipientId);

            var observer = recipientPool.getObserver(recipientId);

            for (Invite invite : invites) {

                notifyNewInviteRecipient(invite, observer);
            }
        } catch (IOException e) {

            throw new RestException("Failed to send missed notifications", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void unsubscribeUserFromSubject(long userId, Long lobbyId) {
        if (recipientPool.isInPool(userId)) {
            SseNotificationObserver observer = ((SseNotificationObserver) recipientPool.getObserver(userId));
            observer.unsubscribeFromSubject(lobbyId);
            observer.removeOnCloseCallback(lobbyId);
        }
    }

    private void notifyNewInviteRecipient(Invite invite, NotificationObserver observer) throws IOException {
        var lobby = invite.getLobby();
        var data = new LobbyCreatedNotificationData(lobby);
        var notification = createNotification(data);
        observer.update(notification);
        inviteService.updateIsDeliveredState(true, invite);
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {

        recipientPool.addObserver(recipientId, emitter);

    }
}
