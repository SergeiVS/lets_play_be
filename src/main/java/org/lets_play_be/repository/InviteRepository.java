package org.lets_play_be.repository;

import org.lets_play_be.entity.Invite.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    @Query("SELECT i FROM Invite i WHERE i.lobby.id=:lobbyId")
    List<Invite> findInvitesByLobbyId(Long lobbyId);

    @Query("SELECT i FROM Invite i WHERE i.lobby.id=:lobbyId AND i.recipient.id=:userId")
    Optional<Invite> findByLobbyIdAndUserId(Long lobbyId, Long userId);

}
