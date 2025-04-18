package org.lets_play_be.notification.notificationService;

import lombok.Getter;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.NotificationObserver;
import org.lets_play_be.notification.NotificationSubject;
import org.lets_play_be.notification.dto.Notification;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LobbySubject implements NotificationSubject {

    @Getter
    private final long lobbyId;

    private final List<NotificationObserver> observers;

    public LobbySubject(long lobbyId) {
        this.lobbyId = lobbyId;
        this.observers = new ArrayList<>();
    }

    @Override
    public void subscribe(NotificationObserver observer) {
        observers.add(observer);

    }

    @Override
    public void unsubscribe(NotificationObserver observer) {
        observers.remove(observer);
    }


    @Override
    public void notifyObservers(Notification notification) {

        observers.parallelStream().forEach(observer -> {
            try {
                observer.update(notification);
            } catch (IOException e) {
                throw new RestException(ErrorMessage.MESSAGE_NOT_SENT.toString(), HttpStatus.BAD_GATEWAY);
            }
        });

    }
}
