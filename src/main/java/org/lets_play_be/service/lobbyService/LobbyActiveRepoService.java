package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.LobbyActive;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyActiveRepoService {
    private final LobbyActiveRepository repository;

    public LobbyActive save(LobbyActive entity) {
        return repository.save(entity);
    }
}
