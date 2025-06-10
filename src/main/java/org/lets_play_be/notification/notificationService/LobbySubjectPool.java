package org.lets_play_be.notification.notificationService;

import org.lets_play_be.notification.NotificationSubject;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class LobbySubjectPool {

    private final Map<Long, NotificationSubject> subjectPool = new ConcurrentHashMap<Long, NotificationSubject>();

    public NotificationSubject getSubject(long lobbyId) {

        return subjectPool.get(lobbyId);
    }

    public void addSubject(LobbySubject subject) {

        subjectPool.put(subject.getLobbyId(), subject);
    }

    public void removeSubject(long lobbyId) {
        subjectPool.remove(lobbyId);
    }

}
