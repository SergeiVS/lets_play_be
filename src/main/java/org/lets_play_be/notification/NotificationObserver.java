package org.lets_play_be.notification;

import org.lets_play_be.notification.dto.Notification;

import java.io.IOException;


public interface NotificationObserver {

    void update(Notification notification) throws IOException;
}


