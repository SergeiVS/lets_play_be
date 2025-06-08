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

    public List<Invite> createListOfNewInvites(List<AppUser> users, LobbyActive lobby, String message) {

        return users.stream().map(user -> new Invite(user, lobby, message)).toList();
    }

    public void changeIsDeliveredState(boolean isDelivered, Invite invite) {

        invite.setDelivered(isDelivered);

        inviteRepository.save(invite);
    }

    public List<Invite> getNotDeliveredInvitesByUserIdl(long userId) {
       return inviteRepository.findNotDeliveredInvitesByUserId(userId);
    }
}
