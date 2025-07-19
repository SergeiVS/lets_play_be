package org.lets_play_be.repository;

import org.lets_play_be.entity.Invite.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    @Query("SELECT i FROM Invite i WHERE i.lobby.id=:lobbyId")
    List<Invite> findInvitesByLobbyId(Long lobbyId);

    @Query("SELECT i FROM Invite i WHERE i.recipient.id=:userId")
    List<Invite> findInvitesByUserId(Long userId);

    @Query("SELECT i FROM Invite  i INNER JOIN  i.recipient u WHERE i.isDelivered=false AND u.id= :userId ")
    List<Invite> findNotDeliveredInvitesByUserId(Long userId);

}
