package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LobbyGetterService {
    private final LobbyRepository repository;
    private final AppUserService userService;

    public Lobby loadLobbyByAuth(Authentication auth) {
        var owner = userService.getUserByEmailOrThrow(auth.getName());
        return loadLobbyByOwnerIdOrThrow(owner);
    }

    public Lobby loadLobbyByOwnerIdOrThrow(AppUser owner) {
        return repository.findLobbyActiveByOwnerId(owner.getId())
                .orElseThrow(() -> new RestException("Current user does not have active lobby", HttpStatus.BAD_REQUEST));
    }

    public Lobby getLobbyByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Lobby found with lobbyId: " + id));
    }

}
