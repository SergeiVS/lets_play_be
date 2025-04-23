package org.lets_play_be.notification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.Notification;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseService sseService;

    private final SseLiveRecipientPool recipientPool;

    private final LobbySubjectPool subjectPool;

    private final AppUserService userService;


    public SseEmitter subscribeForSse(Authentication auth) {

        var recipientId = userService.getUserIdByEmailOrThrow(auth.getName());

        final SseEmitter emitter = sseService.createSseConnection();

        isRecipientNotSubscribed(recipientId);

        createSseObserver(emitter, recipientId);

        return emitter;
    }


    public void subscribeSseObserverForActiveLobby(long recipientId, long lobbyId) {

        isRecipientSubscribed(recipientId);

        NotificationObserver observer = recipientPool.getObserver(recipientId);

        NotificationSubject subject = subjectPool.getSubject(lobbyId);

        subject.subscribe(observer);
    }

    public void notifyLobbyMembers(long lobbyId, Notification notification) {

        NotificationSubject subject = subjectPool.getSubject(lobbyId);

        subject.notifyObservers(notification);
    }

    private void isRecipientNotSubscribed(Long recipientId) {
        if (recipientPool.isInPool(recipientId)) {
            throw new RestException("Recipient with id: " + recipientId + " is already subscribed", HttpStatus.BAD_REQUEST);
        }
    }

    private void isRecipientSubscribed(long recipientId) {
        if (!recipientPool.isInPool(recipientId)) {
            throw new RestException("Recipient with id: " + recipientId + " is not subscribed", HttpStatus.BAD_REQUEST);
        }
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {

        recipientPool.addObserver(recipientId, emitter);
    }
}
