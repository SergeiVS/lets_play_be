package org.lets_play_be.repository;

import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LobbyActiveRepository extends JpaRepository<LobbyActive, Long> {

    boolean existsLobbyActiveByOwner(AppUser owner);
}
