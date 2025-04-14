package org.lets_play_be.service.notificationService.sseNotification;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class SseEmitters {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long recipientId) {

        SseEmitter emitter = new SseEmitter();
        this.emitters.put(recipientId, emitter);

        emitter.onCompletion(() -> this.emitters.remove(recipientId));

        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(recipientId);
        });
        return emitter;
    }

    public SseEmitter remove(Long recipientId) {
        return this.emitters.remove(recipientId);
    }
}
