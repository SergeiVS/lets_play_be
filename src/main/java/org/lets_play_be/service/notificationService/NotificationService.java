package org.lets_play_be.service.notificationService;

import org.lets_play_be.notification.Notification;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    void subscribeUser(AppUser user);

    void unsubscribeUser(AppUser user);

    void notifySubscribers(List<Long> recipientsIds, Notification notification);
}
