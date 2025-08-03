package org.lets_play_be.notification.notificationService.sseNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseLiveRecipientPool {

    private final static Logger log = LoggerFactory.getLogger(SseLiveRecipientPool.class);

    private final Map<Long, SseNotificationObserver> pool = new ConcurrentHashMap<>();

    public void addObserver(long recipientId, SseEmitter emitter) {
        addCallbacksToEmitter(recipientId, emitter);

        pool.put(recipientId, new SseNotificationObserver(emitter));
    }

    public void removeRecipient(final long recipientId) {
        SseNotificationObserver observer = pool.remove(recipientId);

        if (observer != null) {
            observer.getOnCloseCallbacks().values().forEach(Runnable::run);
        }
    }

    public boolean isInPool(final long recipientId) {
        return pool.containsKey(recipientId);
    }

    public SseNotificationObserver getObserver(final long recipientId) {
        return pool.get(recipientId);
    }

    private void addCallbacksToEmitter(long recipientId, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            removeRecipient(recipientId);

            log.info(
                    "Sse connection completed for Recipient with ID: {} | {}",
                    recipientId,
                    emitter
            );
        });

        emitter.onTimeout(() -> log.warn(
                "Sse connection timed out for Recipient with ID: {} | {}",
                recipientId,
                emitter
        ));

        emitter.onError(throwable -> {
            removeRecipient(recipientId);

            log.error(
                    "Sse connection Error for Recipient with ID: {} | error: {}",
                    recipientId,
                    throwable.getMessage()
            );
        });
    }
}
