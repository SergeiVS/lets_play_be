package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LobbyGetterService {
    private final LobbyRepository repository;
    private final AppUserService userService;
    private final InviteService inviteService;

    public Lobby loadLobbyByAuth(Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());
        return loadLobbyByOwnerIdOrThrow(owner);
    }

    public Lobby loadLobbyByOwnerIdOrThrow(AppUser owner) {
        return repository.findLobbyByOwnerId(owner.getId())
                .orElseThrow(() -> new RestException("Current user does not have active lobby", HttpStatus.BAD_REQUEST));
    }

    public Lobby getLobbyByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Lobby found with lobbyId: " + id));
    }

    public Lobby getUserCurrentLobby(AppUser user) {
        Lobby lobby = getLobbyByAcceptedInviteOrNull(user);

        if (lobby != null) {
            return lobby;
        }

        return findOrCreateUserLobby(user);
    }

    public Lobby findOrCreateUserLobby(AppUser user) {
        return repository
                .findLobbyByOwnerId(user.getId())
                .orElseGet(() ->
                        getNewSavedLobby(user)
                );
    }

    private Lobby getNewSavedLobby(AppUser user) {
        Lobby lobby = new Lobby("", OffsetTime.now(), user);

        return repository.save(lobby);
    }

    private Lobby getLobbyByAcceptedInviteOrNull(AppUser user) {
        Invite acceptedInvite = getAcceptedInvite(user);

        if (acceptedInvite != null) {
            return acceptedInvite.getLobby();
        }
        return null;
    }

    private Invite getAcceptedInvite(AppUser user) {
        List<Invite> userInvites = inviteService.findAllUsersInvites(user.getId());

        return userInvites.stream()
                .filter(invite -> invite.getState() == InviteState.ACCEPTED)
                .findFirst()
                .orElse(null);
    }
}
