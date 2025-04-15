package org.lets_play_be.controller;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.controller.api.SseNotificationControllerApi;
import org.lets_play_be.notification.notificationService.sseNotification.SubscribeSseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SseNotificationController implements SseNotificationControllerApi {

    private final SubscribeSseService subscribeSseService;

    @Override
    public SseEmitter openSseStream(Authentication auth) throws IOException {
        return subscribeSseService.openSseStream(auth);
    }

}
