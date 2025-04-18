package org.lets_play_be.notification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationObserver;
import org.lets_play_be.notification.notificationService.sseNotification.SseService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseService sseService;

    private final SseNotificationRecipientPool recipientPool;

    private final LobbySubjectPool subjectPool;

    private final AppUserService userService;

    // TODO add check for LobbySubscriptions
    public SseEmitter subscribeForSse(Authentication auth) {

        var recipientId = userService.getUserIdByEmailOrThrow(auth.getName());

        final SseEmitter emitter = sseService.createSseConnection();

        createSseObserver(emitter, recipientId);

        return emitter;
    }

    private void createSseObserver(SseEmitter emitter, Long recipientId) {
        var observer = new SseNotificationObserver(emitter);
        recipientPool.addObserver(recipientId, observer);
    }
}
