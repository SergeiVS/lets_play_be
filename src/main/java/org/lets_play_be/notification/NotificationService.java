package org.lets_play_be.notification;

import org.lets_play_be.notification.notificationDto.Notification;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService<T> {

    void subscribeUser(Observer<T> observer);

    void unsubscribeUser(Observer<T> observer);

    void notifySubscribers(List<Long> recipientsIds, Notification notification);
}
