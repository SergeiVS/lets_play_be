package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.Getter;
import org.lets_play_be.notification.EventService;
import org.lets_play_be.notification.notificationDto.Notification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class SseEmitters implements EventService<SseEmitter> {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();


    public void remove(Long recipientId) {
        this.emitters.remove(recipientId);
    }

    @Override
    public SseEmitter create(Long recipientId) {

        SseEmitter emitter = new SseEmitter();
        this.emitters.put(recipientId, emitter);

        emitter.onCompletion(() -> this.emitters.remove(recipientId));

        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(recipientId);
        });

        emitter.onError(throwable -> {
            remove(recipientId);
            emitter.completeWithError(throwable);
        });

        return emitter;
    }

    @Override
    public void sendEvent(Notification notification, List<Long> recipientsIds) {

        for (Long recipientId : recipientsIds) {
            SseEmitter emitter = this.emitters.get(recipientId);
            if (emitter != null) {
                try {
                    emitter.send(notification);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }
    }
}
