package org.lets_play_be.repository;

import org.lets_play_be.entity.lobby.LobbyPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LobbyPresetRepository extends JpaRepository<LobbyPreset, Long> {

    @Deprecated
    @Query("SELECT lp FROM LobbyPreset  lp WHERE lp.owner.id = :id")
    List<LobbyPreset> findByOwnerId(Long id);

    @Query("SELECT lp FROM LobbyPreset  lp WHERE lp.owner.id = :ownerId")
    Optional<LobbyPreset> findUniqueByOwnerId(Long ownerId);
}
