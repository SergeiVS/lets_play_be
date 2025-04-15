package org.lets_play_be.notification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.notification.notificationDto.Notification;

import java.util.List;

@RequiredArgsConstructor
public abstract class Observer <T>{

   private final EventService<T> eventService;

    public void update(Notification notification, List<Long> recipientsIds){
        eventService.sendEvent(notification, recipientsIds);
    };

}
