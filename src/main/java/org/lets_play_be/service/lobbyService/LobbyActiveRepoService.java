package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LobbyActiveRepoService {
    private final LobbyActiveRepository repository;

    public Optional<LobbyActive> findById(Long id) {
        return repository.findById(id);
    }

    public LobbyActive save(LobbyActive entity) {
        return repository.save(entity);
    }

    public boolean existByOwner(AppUser owner){
        return repository.existsLobbyActiveByOwner(owner);
    }
}
