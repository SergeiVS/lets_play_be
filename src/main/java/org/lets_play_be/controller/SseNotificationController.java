package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.SseNotificationControllerApi;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseNotificationController implements SseNotificationControllerApi {

   private final SseNotificationService notificationService;

    @Override
    public SseEmitter openSseStream(Authentication auth) {
        return notificationService.subscribeForSse(auth);
    }

}
