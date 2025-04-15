package org.lets_play_be.notification;

import jdk.jfr.Event;
import org.lets_play_be.notification.notificationDto.Notification;

import java.util.List;

public interface EventService <T> {

    T create(Long userId);
    void remove(Long userId);
    void sendEvent(Notification notification, List<Long> recipientsIds);
}
