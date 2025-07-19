package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
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

        var recipient = userService.getUserByEmailOrThrow(auth.getName());

        final SseEmitter emitter = sseService.createSseConnection();

        createSseObserver(emitter, recipient.getId());

        return emitter;
    }


    public void subscribeSseObserverForActiveLobby(long recipientId, long lobbyId) {

        try {
            if (recipientPool.isInPool(recipientId)) {

                var observer = ((SseNotificationObserver) recipientPool.getObserver(recipientId));

                var subject = ((LobbySubject) subjectPool.getSubject(lobbyId));

                observer.addOnCloseCallback(lobbyId, subject.removeObserver(observer));

                subject.subscribe(observer);
            }
        } catch (RuntimeException e) {
            throw new RestException("Subscription for Lobby failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void notifyLobbyMembers(long lobbyId, NotificationData data) {
        try {
            var subject = subjectPool.getSubject(lobbyId);

            var notification = createNotification(data);

            subject.notifyObservers(notification);

        } catch (RuntimeException e) {
            throw new RestException("Members Notification failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void unsubscribeUserFromSubject(long userId, Long lobbyId) {
        if (recipientPool.isInPool(userId)) {
            var observer = ((SseNotificationObserver) recipientPool.getObserver(userId));
            observer.unsubscribeFromSubject(lobbyId);
            observer.removeOnCloseCallback(lobbyId);
        }
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {

        recipientPool.addObserver(recipientId, emitter);

    }
}
