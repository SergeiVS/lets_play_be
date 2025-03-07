package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyPresetRepoService {
    private final LobbyPresetRepository repository;
}
