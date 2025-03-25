package org.lets_play_be.service.InviteService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.notification.Invite;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.InviteRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InviteService {
    private final InviteRepository inviteRepository;

    public Invite createInvite(String message, AppUser user, LobbyActive lobby) {
        return new Invite(user, lobby, message);
    }
}
