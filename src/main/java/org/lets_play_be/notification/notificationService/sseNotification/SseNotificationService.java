package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import static org.lets_play_be.notification.NotificationFactory.createNotification;

@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final SseService sseService;

    private final SseLiveRecipientPool recipientPool;

    private final LobbySubjectPool subjectPool;

    private final AppUserService userService;


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

    public void unsubscribeUserFromSubject(long userId, Long lobbyId) {
        if (recipientPool.isInPool(userId)) {
            SseNotificationObserver observer = ((SseNotificationObserver) recipientPool.getObserver(userId));
            observer.unsubscribeFromSubject(lobbyId);
            observer.removeOnCloseCallback(lobbyId);
        }
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {

        recipientPool.addObserver(recipientId, emitter);

    }
}
