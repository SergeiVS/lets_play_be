package org.lets_play_be.notification;

import org.lets_play_be.notification.dto.Notification;

public interface NotificationSubject {

    void subscribe(NotificationObserver observer);

    void unsubscribe(NotificationObserver observer);

    void notifyObservers(Notification notification);
}
