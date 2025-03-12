package org.lets_play_be.repository;

import org.lets_play_be.entity.LobbyPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LobbyPresetRepository extends JpaRepository<LobbyPreset, Long> {

    @Query("SELECT lp FROM LobbyPreset  lp WHERE lp.owner.id = :id")
    List<LobbyPreset> findByOwnerId(Long id);
}
