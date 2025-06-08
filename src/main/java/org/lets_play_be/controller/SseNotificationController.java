package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.SseNotificationControllerApi;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SseNotificationController implements SseNotificationControllerApi {

   private final SseNotificationService notificationService;
    private final SseNotificationService sseNotificationService;

    @Override
    public SseEmitter openSseStream(Authentication auth) {
        return notificationService.subscribeForSse(auth);
    }

    @Override
    public void getMissedNotifications(Authentication authentication) throws IOException {
        sseNotificationService.sendMissedNotifications(authentication);
    }

}
