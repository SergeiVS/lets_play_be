package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.LobbyPreset;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyPresetRepoService {
    private final LobbyPresetRepository repository;

    public LobbyPreset save(LobbyPreset lobbyPreset) {
        return repository.save(lobbyPreset);
    }

    public List<LobbyPreset> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId);
    }
}
