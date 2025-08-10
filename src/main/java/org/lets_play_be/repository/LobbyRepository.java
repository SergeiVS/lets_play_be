package org.lets_play_be.repository;

import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {

    boolean existsLobbyActiveByOwner(AppUser owner);

    @Query("SELECT l.id FROM Lobby l")
    List<Long> findAllLobbyIds();

    Optional<Lobby> findLobbyActiveByOwnerId(long ownerId);
}
