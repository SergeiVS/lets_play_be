package org.lets_play_be.notification.notificationService.sseNotification;

import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.NotificationObserver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseLiveRecipientPool {

    private final Map<Long, NotificationObserver> pool = new ConcurrentHashMap<>();

    public void addObserver(long recipientId, SseEmitter emitter) {

        addCallbacksToEmitter(recipientId, emitter);

        NotificationObserver observer = new SseNotificationObserver(emitter);

        pool.put(recipientId, observer);
    }


    public void removeRecipient(final long recipientId) {

        SseNotificationObserver observer = (SseNotificationObserver) pool.remove(recipientId);
        observer.getOnCloseCallbacks().values().forEach(Runnable::run);

    }

    public boolean isInPool(final long recipientId) {
        return pool.containsKey(recipientId);
    }


    public NotificationObserver getObserver(final long recipientId) {

        return pool.get(recipientId);
    }

    private void addCallbacksToEmitter(long recipientId, SseEmitter emitter) {

        emitter.onCompletion(() -> removeRecipient(recipientId));

        emitter.onTimeout(() -> removeRecipient(recipientId));

        emitter.onError(throwable -> {

            removeRecipient(recipientId);

            throw new RestException("Sse connection error. " + throwable.getMessage(), HttpStatus.BAD_GATEWAY);
        });
    }
}
