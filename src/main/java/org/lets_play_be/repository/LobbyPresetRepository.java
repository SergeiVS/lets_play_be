package org.lets_play_be.repository;

import org.lets_play_be.entity.lobby.LobbyPreset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyPresetRepository extends JpaRepository<LobbyPreset, Long> {
}
