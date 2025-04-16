package org.lets_play_be.notification.notificationService.sseNotification;

import org.lets_play_be.notification.NotificationObserver;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SseLiveRecipientPool {

    private final Map<Long, NotificationObserver> pool = new ConcurrentHashMap<>();

    public void addRecipient(long recipientId, SseEmitter emitter) {

        NotificationObserver observer = new SseNotificationObserver(emitter);

        pool.put(recipientId, observer);
    }

    public void removeRecipient(final long recipientId) {

        pool.remove(recipientId);
    }


    public NotificationObserver getRecipient(final long recipientId) {

        return pool.get(recipientId);
    }
}
