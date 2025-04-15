package org.lets_play_be.notification.notificationService.sseNotification;

import org.lets_play_be.notification.EventService;
import org.lets_play_be.notification.Observer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SseNotificationObserver extends Observer<SseEmitter> {

    public SseNotificationObserver(EventService<SseEmitter> eventService) {
        super(eventService);
    }
}
