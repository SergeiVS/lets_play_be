package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.notification.NotificationService;
import org.lets_play_be.notification.Observer;
import org.lets_play_be.notification.notificationDto.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SseNotificationService implements NotificationService<SseEmitter> {

    private final List<Observer<SseEmitter>> observers = new ArrayList<>();


    @Override
    public void subscribeUser(Observer<SseEmitter> observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscribeUser(Observer<SseEmitter> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifySubscribers(List<Long> recipientsIds, Notification notification) {
        observers.forEach(observer -> {observer.update(notification, recipientsIds);});
    }

}
