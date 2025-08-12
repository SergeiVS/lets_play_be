package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyGetterServiceTest {

    @Mock
    private LobbyRepository repository;

    @Mock
    private AppUserService userService;

    @InjectMocks
    private LobbyGetterService lobbyGetter;

    private AppUser owner;
    private Lobby lobby;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        owner = new AppUser(1L, "Name", "email", "", "");
        lobby = new Lobby(1L, "", OffsetTime.now().plusHours(1), owner);
        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), "password", null);
    }

    @AfterEach
    void tearDown() {
        owner = null;
        auth = null;
        lobby = null;
    }

    @Test
    void loadLobbyByAuth_Success() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(lobby));

        var result = lobbyGetter.loadLobbyByAuth(auth);

        assertEquals(lobby, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void loadLobbyByAuth_Throws_OwnerNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

       assertThrows(UsernameNotFoundException.class, ()->lobbyGetter.loadLobbyByAuth(auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(repository);
    }

    @Test
    void loadLobbyByAuth_Throws_LobbyNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.empty());

        assertThrows(RestException.class, ()->lobbyGetter.loadLobbyByAuth(auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void loadLobbyByOwnerIdOrThrow_Success() {
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(lobby));

        var result = lobbyGetter.loadLobbyByOwnerIdOrThrow(owner);

        assertEquals(lobby, result);

        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void loadLobbyByOwnerIdOrThrow_Throws_LobbyNotFound() {
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.empty());

        assertThrows(RestException.class, () -> lobbyGetter.loadLobbyByOwnerIdOrThrow(owner));

        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getLobbyByIdOrThrow_Success() {
        when(repository.findById(lobby.getId())).thenReturn(Optional.of(lobby));

        var result = lobbyGetter.getLobbyByIdOrThrow(lobby.getId());

        assertEquals(lobby, result);

        verify(repository, times(1)).findById(lobby.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getLobbyByIdOrThrow_Throw_LobbyNotFound() {
        when(repository.findById(lobby.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> lobbyGetter.getLobbyByIdOrThrow(lobby.getId()),
                "No Lobby found with lobbyId: " + lobby.getId()
        );

        verify(repository, times(1)).findById(lobby.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getUserCurrentLobby() {
    }

    @Test
    void findOrCreateUserLobby() {
    }
}