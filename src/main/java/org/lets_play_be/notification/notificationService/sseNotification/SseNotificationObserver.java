package org.lets_play_be.notification.notificationService.sseNotification;

import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.dto.Notification;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public class SseNotificationObserver implements NotificationObserver {

    private final SseEmitter emitter;

    public SseNotificationObserver(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void update(Notification notification) throws IOException {
        emitter.send(notification, MediaType.TEXT_EVENT_STREAM);
    }
}
