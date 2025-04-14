package org.lets_play_be.service.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.notification.Notification;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.service.notificationService.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SseNotificationService implements NotificationService {

    private final SseEmitters emitters;

    @Override
    public void subscribeUser(AppUser user) {

    }

    @Override
    public void unsubscribeUser(AppUser user) {

    }

    @Override
    public void notifySubscribers(List<Long> recipientsIds, Notification notification) {
    }

}
