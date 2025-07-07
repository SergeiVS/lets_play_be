package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.Data;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.dto.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class SseNotificationObserver implements NotificationObserver {

    private final SseEmitter emitter;

    private final Map<Long, Runnable> onCloseCallbacks = new HashMap<>();

    public SseNotificationObserver(SseEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void update(Notification notification) throws IOException {
        emitter.send(notification);
    }

    public void addOnCloseCallback(Long lobbyId, Runnable runnable) {

        onCloseCallbacks.put(lobbyId, runnable);
    }

    public void removeOnCloseCallback(Long lobbyId) {
        onCloseCallbacks.remove(lobbyId);
    }

    public void unsubscribeFromSubject(Long lobbyId) {
        onCloseCallbacks.get(lobbyId).run();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SseNotificationObserver observer)) return false;
        return Objects.equals(getEmitter(), observer.getEmitter());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getEmitter());
    }
}
