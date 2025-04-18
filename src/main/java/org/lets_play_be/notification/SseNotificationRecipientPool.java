package org.lets_play_be.notification;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Component
public class SseNotificationRecipientPool {

    private final Map<Long, NotificationObserver> observers = new HashMap<>();

    public void addObserver(long recipientId, NotificationObserver observer) {

        observers.putIfAbsent(recipientId, observer);
    }

    public void removeObserver(long recipientId) {
        observers.remove(recipientId);
    }

    public boolean isInPool(long recipientId) {
        return observers.containsKey(recipientId);
    }

    public NotificationObserver getObserver(long recipientId) {
        return observers.get(recipientId);
    }
}
