package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.NotificationFactory;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.dto.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

//TODO Complete method
    public SseEmitter createSseConnection() {
        SseEmitter emitter = new SseEmitter(6000L);

        try {
            Notification notification = NotificationFactory.createNotification(new MessageNotificationData("Connection build"));
            emitter.send(notification);
        } catch (Exception e) {
            throw new RestException("Connection is not created", HttpStatus.BAD_GATEWAY);
        }
        return emitter;
    }

}
