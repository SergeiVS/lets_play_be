package org.lets_play_be.notification.notificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitSubjectsOnStart {

    private final LobbyActiveRepository repository;

    private final LobbySubjectPool pool;

    @PostConstruct
    public void init() {
        List<Long> lobbyIds = repository.findAllLobbyIds();
        lobbyIds.forEach(lobbyId -> {
            pool.addSubject(new LobbySubject(lobbyId));
            System.out.println(pool.getSubject(lobbyId).toString());
        });

        ;
    }
}
