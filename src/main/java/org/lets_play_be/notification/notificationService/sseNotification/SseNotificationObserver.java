package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.Getter;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.dto.Notification;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SseNotificationObserver implements NotificationObserver {

    private final SseEmitter emitter;

    Map<Long, Runnable> onCloseCallbacks = new HashMap<>();

    public SseNotificationObserver(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void update(Notification notification) throws IOException {
        emitter.send(notification, MediaType.TEXT_EVENT_STREAM);
    }

    public void addOnCloseCallback(long id, Runnable runnable) {
        onCloseCallbacks.put(id, runnable);
    }

    public void removeOnCloseCallback(long id) {
        onCloseCallbacks.remove(id);
    }
}
