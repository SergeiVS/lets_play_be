package org.lets_play_be.notification.notificationService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.notification.NotificationSubject;

import java.util.Map;

@RequiredArgsConstructor
public class LobbySubjectPool {

    private final Map<Long, NotificationSubject> subjectPool;

    public NotificationSubject getSubject(long lobbyId) {

        return subjectPool.get(lobbyId);
    }

    public void addSubject(LobbySubject subject) {

        subjectPool.put(subject.getLobbyId(), subject);
    }
}
