package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyUserService {
    private final AppUserService userService;
    private final LobbyRepository repository;
    private final LobbyGetterService lobbyGetter;

    @Transactional
    public Lobby addUsers(ChangeUsersListRequest request, Authentication auth) {
        var lobby = lobbyGetter.loadLobbyByAuth(auth);

        List<Invite> newInvites = getNewInvitesList(request.usersIds(), request.message(), lobby);
        lobby.getInvites().addAll(newInvites);

        return repository.save(lobby);
    }

    @Transactional
    public Lobby removeUsers(ChangeUsersListRequest request, Authentication auth) {
        var lobby = lobbyGetter.loadLobbyByAuth(auth);

        List<Long> kickedUsersIds = request.usersIds();

        lobby.getInvites().removeIf(invite -> kickedUsersIds.contains(invite.getRecipient().getId()));

        return repository.save(lobby);
    }

    public Lobby loadLobbyByOwnerIdOrThrow(AppUser owner) {
        return repository.findLobbyActiveByOwnerId(owner.getId())
                .orElseThrow(() -> new RestException("Current user does not have active lobby", HttpStatus.BAD_REQUEST));
    }

    private List<Invite> getNewInvitesList(List<Long> usersId, String message, Lobby lobbyForSave) {
        List<AppUser> users = userService.getUsersListByIds(usersId);

        return users.stream().map(user -> new Invite(user, lobbyForSave, message)).toList();
    }
}

