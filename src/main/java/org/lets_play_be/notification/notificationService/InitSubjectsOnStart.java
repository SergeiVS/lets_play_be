package org.lets_play_be.notification.notificationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.repository.LobbyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitSubjectsOnStart {

    private final LobbyRepository repository;

    private final LobbySubjectPool pool;

    @PostConstruct
    public void init() {

        List<Long> lobbyIds = repository.findAllLobbyIds();

        lobbyIds.forEach(lobbyId -> {
            pool.addSubject(new LobbySubject(lobbyId));

            if(pool.getSubject(lobbyId) instanceof LobbySubject subject) {
                log.info("Subject created for Lobby with Id: {}", subject.getLobbyId());
            }
        });
    }
}
