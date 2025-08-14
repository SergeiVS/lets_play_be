package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.appUserService.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.lets_play_be.notification.NotificationFactory.createNotification;

@Service
@RequiredArgsConstructor
public class SseNotificationService {

    private final static Logger log = LoggerFactory.getLogger(SseNotificationService.class);

    private final SseService sseService;
    private final AppUserService userService;
    private final SseLiveRecipientPool recipientPool;
    private final LobbySubjectPool subjectPool;


    public SseEmitter subscribeForSse(Authentication auth) {
        var recipient = userService.getUserByEmailOrThrow(auth.getName());

        if (recipientPool.isInPool(recipient.getId())) {
            return recipientPool.getObserver(recipient.getId()).getEmitter();
        }

        final SseEmitter emitter = sseService.createSseConnection();

        createSseObserver(emitter, recipient.getId());

        log.info("Sse connection created for Recipient with ID: {}", recipient.getId());

        return emitter;
    }


    public void subscribeSseObserverToLobby(long recipientId, long lobbyId) {
        try {
            var observer = recipientPool.getObserver(recipientId);

            var subject = ((LobbySubject) subjectPool.getSubject(lobbyId));

            observer.addOnCloseCallback(lobbyId, subject.removeObserver(observer));

            subject.subscribe(observer);

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
            var observer = recipientPool.getObserver(userId);

            observer.unsubscribeFromSubject(lobbyId);
            observer.removeOnCloseCallback(lobbyId);
        }
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {
        recipientPool.addObserver(recipientId, emitter);
    }
}
