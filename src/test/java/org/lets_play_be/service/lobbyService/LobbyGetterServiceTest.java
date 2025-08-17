package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
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

    @Mock
    private InviteService inviteService;

    @InjectMocks
    private LobbyGetterService lobbyGetter;

    private AppUser owner;
    private AppUser otherUser1;
    private AppUser otherUser2;
    private Lobby ownerLobby;
    private Lobby ownerBlancLobby;
    private Lobby otherUser1Lobby;
    private Lobby otherUser2Lobby;
    private Invite ownerAcceptedInvite;
    private Invite ownerNotAcceptedInvite;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        owner = new AppUser(1L, "Name", "email", "", "");
        otherUser1 = new AppUser(2L, "Name1", "email1", "", "");
        otherUser2 = new AppUser(3L, "Name2", "email2", "", "");

        ownerLobby = new Lobby(1L, "Title", OffsetTime.now().plusHours(1), owner);
        ownerBlancLobby = new Lobby("", OffsetTime.now(), owner);
        otherUser1Lobby = new Lobby(2L, "Title2", OffsetTime.now().plusHours(2), otherUser1);
        otherUser2Lobby = new Lobby(3L, "Title3", OffsetTime.now().plusHours(3), otherUser2);

        ownerAcceptedInvite = new Invite(
                1L,
                owner,
                OffsetDateTime.now(),
                true,
                true,
                InviteState.ACCEPTED,
                "Message1",
                0,
                otherUser1Lobby);
        ownerNotAcceptedInvite = new Invite(
                2L,
                owner,
                OffsetDateTime.now(),
                true,
                true,
                InviteState.PENDING,
                "Message2",
                0,
                otherUser2Lobby);

        otherUser1Lobby.getInvites().add(ownerAcceptedInvite);
        otherUser2Lobby.getInvites().add(ownerNotAcceptedInvite);

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), "password", null);
    }

    @AfterEach
    void tearDown() {
        owner = null;
        otherUser1 = null;
        otherUser2 = null;

        ownerLobby = null;
        ownerBlancLobby = null;
        otherUser1Lobby = null;
        otherUser2Lobby = null;

        ownerAcceptedInvite = null;
        ownerNotAcceptedInvite = null;

        auth = null;
    }

    @Test
    void loadLobbyByAuth_Success() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(ownerLobby));

        var result = lobbyGetter.loadLobbyByAuth(auth);

        assertEquals(ownerLobby, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void loadLobbyByAuth_Throws_OwnerNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> lobbyGetter.loadLobbyByAuth(auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(repository);
    }

    @Test
    void loadLobbyByAuth_Throws_LobbyNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.empty());

        assertThrows(RestException.class, () -> lobbyGetter.loadLobbyByAuth(auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void loadLobbyByOwnerIdOrThrow_Success() {
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(ownerLobby));

        var result = lobbyGetter.loadLobbyByOwnerIdOrThrow(owner);

        assertEquals(ownerLobby, result);

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
        when(repository.findById(ownerLobby.getId())).thenReturn(Optional.of(ownerLobby));

        var result = lobbyGetter.getLobbyByIdOrThrow(ownerLobby.getId());

        assertEquals(ownerLobby, result);

        verify(repository, times(1)).findById(ownerLobby.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getLobbyByIdOrThrow_Throw_LobbyNotFound() {
        when(repository.findById(ownerLobby.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> lobbyGetter.getLobbyByIdOrThrow(ownerLobby.getId()),
                "No Lobby found with lobbyId: " + ownerLobby.getId()
        );

        verify(repository, times(1)).findById(ownerLobby.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getUserCurrentLobby_Success_WhenNotAcceptedInvitesFound() {
        when(inviteService.findAllUsersInvites(owner.getId())).thenReturn(List.of(ownerNotAcceptedInvite));
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(ownerLobby));

        var result = lobbyGetter.getUserCurrentLobby(owner);
        var expectedResult = ownerLobby;

        assertEquals(expectedResult, result);
        verify(inviteService, times(1)).findAllUsersInvites(owner.getId());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getUserCurrentLobby_Success_WhenNotAcceptedInvitesFound_OwnerHasNotAnyLobby() {
        when(inviteService.findAllUsersInvites(owner.getId())).thenReturn(List.of(ownerNotAcceptedInvite));
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.empty());
        when(repository.save(any(Lobby.class))).thenReturn(ownerBlancLobby);

        var result = lobbyGetter.getUserCurrentLobby(owner);

        assertEquals(ownerBlancLobby, result);
        verify(inviteService, times(1)).findAllUsersInvites(owner.getId());
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verify(repository, times(1)).save(any(Lobby.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getUserCurrentLobby_Success_WhenAcceptedInviteFound() {
        when(inviteService.findAllUsersInvites(owner.getId())).thenReturn(List.of(ownerNotAcceptedInvite, ownerAcceptedInvite));

        var result = lobbyGetter.getUserCurrentLobby(owner);

        assertEquals(otherUser1Lobby, result);
        verify(inviteService, times(1)).findAllUsersInvites(owner.getId());
        verifyNoInteractions(repository);
        verifyNoMoreInteractions(inviteService);
    }

    @Test
    void findOrCreateUserLobby_Success_LobbyFound() {
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.of(ownerLobby));

        assertEquals(ownerLobby, lobbyGetter.findOrCreateUserLobby(owner));
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findOrCreateUserLobby_Success_LobbyNotFound() {
        when(repository.findLobbyByOwnerId(owner.getId())).thenReturn(Optional.empty());
        when(repository.save(any(Lobby.class))).thenReturn(ownerBlancLobby);

        assertEquals(ownerBlancLobby, lobbyGetter.findOrCreateUserLobby(owner));
        verify(repository, times(1)).findLobbyByOwnerId(owner.getId());
        verify(repository, times(1)).save(any(Lobby.class));
        verifyNoMoreInteractions(repository);
    }
}