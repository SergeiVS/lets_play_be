package org.lets_play_be.service.InviteService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.InviteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;

    public Invite createInvite(String message, AppUser user, LobbyActive lobby) {
        return new Invite(user, lobby, message);
    }

    public List<Invite> createListOfNewInvites(List<AppUser> users, LobbyActive lobby, String message) {
        return users.stream().map(user -> new Invite(user, lobby, message)).toList();
    }

    public Invite changeIsDeliveredState(boolean isDelivered, long userid, long lobbyId) {

        Invite invite = getInviteByLobbyAndUserOrThrow(userid, lobbyId);

        invite.setDelivered(isDelivered);

        return inviteRepository.save(invite);

    }

    public List<Invite> getInvitesByLobbyId(Long lobbyId) {
        return inviteRepository.findInvitesByLobbyId(lobbyId);
    }

    public List<Invite> saveAllInvites(List<Invite> invites) {
        return inviteRepository.saveAll(invites);
    }

    public Invite saveInvite(Invite invite) {
        return inviteRepository.save(invite);
    }

    public Optional<Invite> findInviteById(Long id) {
        return inviteRepository.findById(id);
    }


    private Invite getInviteByLobbyAndUserOrThrow(long userid, long lobbyId) {
        return inviteRepository.findByLobbyIdAndUserId(lobbyId, userid)
                .orElseThrow(() -> new RestException("No such User or Lobby exist", HttpStatus.BAD_REQUEST));
    }

}
