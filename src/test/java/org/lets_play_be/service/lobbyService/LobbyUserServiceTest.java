package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyUserServiceTest {

    @Mock
    LobbyRepository repository;

    @Mock
    AppUserService userService;

    @Mock
    LobbyGetterService getter;

    @InjectMocks
    LobbyUserService service;

    Authentication auth;
    AppUser owner;
    AppUser user1;
    AppUser user2;
    AppUser user3;
    AppUser user4;
    AppUser user5;

    ChangeUsersListRequest request;

    Invite invite1;
    Invite invite2;
    Invite invite3;
    Invite invite4;
    Invite invite5;

    Lobby lobbyForSave;
    Lobby savedLobby;

    String message = "message";
    String newInviteMessage = "newInviteMessage";
    String timeString = "18:00:00+00:00";

    @BeforeEach
    void setUp() {
        owner = new AppUser(10L, "Name", "email@email.com", "password", "url");
        user1 = new AppUser(11L, "Name1", "email@email.com", "password1", "url1");
        user2 = new AppUser(12L, "Name2", "email2@email.com", "password2", "url2");
        user3 = new AppUser(13L, "Name3", "email3@email.com", "password3", "url3");
        user4 = new AppUser(14L, "User4", "email4@email.com", "password4", "Url4");
        user5 = new AppUser(15L, "User5", "email5@email.com", "password5", "Url5");

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), owner.getPassword(), null);

        invite1 = new Invite(1L, user1, lobbyForSave, message);
        invite2 = new Invite(2L, user2, lobbyForSave, message);
        invite3 = new Invite(3L, user3, lobbyForSave, message);
        invite4 = new Invite(4L, user4, lobbyForSave, newInviteMessage);
        invite5 = new Invite(5L, user5, lobbyForSave, newInviteMessage);

        lobbyForSave = new Lobby(1L, "Title", OffsetTime.parse(timeString), owner);
        lobbyForSave.getInvites().addAll(List.of(invite1, invite2, invite3));

        savedLobby = new Lobby(1L, "Title", OffsetTime.parse(timeString), owner);
        savedLobby.getInvites().addAll(List.of(invite1, invite2, invite3, invite4, invite5));

        request = new ChangeUsersListRequest(newInviteMessage, List.of(user4.getId(), user5.getId()));
    }

    @AfterEach
    void tearDown() {
        owner = null;
        user1 = null;
        user2 = null;
        user3 = null;
        user4 = null;
        user5 = null;
        auth = null;
        invite1 = null;
        invite2 = null;
        invite3 = null;
        invite4 = null;
        invite5 = null;
        savedLobby = null;
        lobbyForSave = null;
        request = null;
    }

    @Test
    void addUsers_Success() {
        when(getter.loadLobbyByAuth(auth)).thenReturn(lobbyForSave);
        when(userService.getUsersListByIds(request.usersIds())).thenReturn(List.of(user4, user5));
        when(repository.save(savedLobby)).thenReturn(savedLobby);

        var result = service.addUsers(request, auth);

        assertEquals(savedLobby, result);
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verify(userService, times(1)).getUsersListByIds(request.usersIds());
        verify(repository, times(1)).save(savedLobby);
        verifyNoMoreInteractions(getter);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void addUsers_Throws_ByLobbyLoading(Class<? extends RuntimeException> e) {
        when(getter.loadLobbyByAuth(auth)).thenThrow(e);

        assertThrows(e, () -> service.addUsers(request, auth));
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verifyNoMoreInteractions(getter);
        verifyNoInteractions(userService);
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void addUsers_Throws_UsersLoadingFailed(Class<? extends RuntimeException> e) {
        when(getter.loadLobbyByAuth(auth)).thenReturn(lobbyForSave);
        when(userService.getUsersListByIds(request.usersIds())).thenThrow(e);

        assertThrows(e, () -> service.addUsers(request, auth));
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verify(userService, times(1)).getUsersListByIds(request.usersIds());
        verifyNoMoreInteractions(getter);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(repository);
    }

    @Test
    void removeUsers_Success() {
        when(getter.loadLobbyByAuth(auth)).thenReturn(savedLobby);
        when(repository.save(lobbyForSave)).thenReturn(lobbyForSave);

        var result = service.removeUsers(request, auth);

        assertEquals(lobbyForSave, result);
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verify(repository, times(1)).save(savedLobby);
        verifyNoMoreInteractions(getter);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void removeUsers_Success_NotThrowsWhenRequestContainsInvalidUserIds() {
        var requestWithInvalidIds = new ChangeUsersListRequest(newInviteMessage, List.of(user4.getId(), user5.getId(), 23L, 55L));

        when(getter.loadLobbyByAuth(auth)).thenReturn(savedLobby);
        when(repository.save(lobbyForSave)).thenReturn(lobbyForSave);

        var result = service.removeUsers(requestWithInvalidIds, auth);

        assertEquals(lobbyForSave, result);
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verify(repository, times(1)).save(savedLobby);
        verifyNoMoreInteractions(getter);
        verifyNoMoreInteractions(repository);
    }


    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void removeUsers_Throws_UsersLoadingFailed(Class<? extends RuntimeException> e) {
        when(getter.loadLobbyByAuth(auth)).thenThrow(e);

        assertThrows(e, () -> service.removeUsers(request, auth));
        verify(getter, times(1)).loadLobbyByAuth(auth);
        verifyNoMoreInteractions(getter);
        verifyNoInteractions(repository);
    }
}