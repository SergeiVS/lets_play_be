package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.LobbyPreset;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LobbyPresetRepoService {
    private final LobbyPresetRepository repository;

    public Optional<LobbyPreset> findById(Long id) {
        return repository.findById(id);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public Optional<LobbyPreset> deleteById(Long id) {
        return repository.findById(id);
    }

    public LobbyPreset save(LobbyPreset lobbyPreset) {
        return repository.save(lobbyPreset);
    }

    public List<LobbyPreset> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId);
    }
}
