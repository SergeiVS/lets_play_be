package org.lets_play_be.repository;

import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LobbyActiveRepository extends JpaRepository<LobbyActive, Long> {

    boolean existsLobbyActiveByOwner(AppUser owner);

    @Query("SELECT l.id FROM LobbyActive l")
    List<Long> findAllLobbyIds();
}
