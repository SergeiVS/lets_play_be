package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.lobbyDto.ActivateLobbyRequest;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyRequest;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    @Mock
    private LobbyRepository repository;

    @Mock
    private LobbyBaseUpdateService baseUpdateService;

    @Mock
    private LobbyUserService lobbyUserService;

    @Mock
    private LobbyGetterService getterService;

    @Mock
    private LobbyNotificationsService lobbyNotificationsService;

    @Mock
    private AppUserService userService;

    @Mock
    private InviteService inviteService;

    @Mock
    private SseNotificationService notificationService;

    @InjectMocks
    private LobbyService service;

    Authentication auth;
    private AppUser owner;
    private AppUser user1;
    private AppUser user2;
    private AppUser user3;
    private AppUser user4;
    private AppUser user5;

    private ChangeUsersListRequest changeUsersListRequest;
    private ActivateLobbyRequest activationRequest;
    private UpdateLobbyRequest updateLobbyRequest;

    private Lobby lobby;
    private Lobby changedLobby;
    private Lobby blancLobby;

    private Invite invite1;
    private Invite invite2;
    private Invite invite3;
    private Invite invite4;
    private Invite invite5;

    private final String message = "message";
    private final String newInviteMessage = "newInviteMessage";
    private final String newTitle = "newTitle";
    private final String newTime = "22:00:00+01:00";

    @BeforeEach
    void setUp() {
        owner = new AppUser(10L, "Name", "email@email.com", "password", "url");
        user1 = new AppUser(11L, "Name1", "email@email.com", "password1", "url1");
        user2 = new AppUser(12L, "Name2", "email2@email.com", "password2", "url2");
        user3 = new AppUser(13L, "Name3", "email3@email.com", "password3", "url3");
        user4 = new AppUser(14L, "User4", "email4@email.com", "password4", "Url4");
        user5 = new AppUser(15L, "User5", "email5@email.com", "password5", "Url5");

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), owner.getPassword(), null);

        invite1 = new Invite(1L, user1, lobby, message);
        invite1.setState(InviteState.ACCEPTED);
        invite2 = new Invite(2L, user2, lobby, message);
        invite2.setState(InviteState.PENDING);
        invite3 = new Invite(3L, user3, lobby, message);
        invite3.setState(InviteState.DELAYED);
        invite3.setDelayedFor(3);
        invite4 = new Invite(4L, user4, lobby, newInviteMessage);
        invite5 = new Invite(5L, user5, lobby, newInviteMessage);

        OffsetTime time = OffsetTime.now().plusHours(1);

        lobby = new Lobby(1L, "Title", time, owner);
        lobby.setType(LobbyType.INACTIVE);
        lobby.getInvites().addAll(List.of(invite1, invite2, invite3));

        changedLobby = new Lobby(1L, "Title", time, owner);
        changedLobby.getInvites().addAll(List.of(invite1, invite2, invite3));

        blancLobby = new Lobby(1L, "", OffsetTime.now(), owner);
        blancLobby.setType(LobbyType.INACTIVE);

        activationRequest = new ActivateLobbyRequest(newInviteMessage);
        changeUsersListRequest = new ChangeUsersListRequest(newInviteMessage, List.of(user4.getId(), user5.getId()));
        updateLobbyRequest = new UpdateLobbyRequest(lobby.getId(), newTitle, newTime);
    }

    @AfterEach
    void tearDown() {
        owner = null;
        user1 = null;
        user2 = null;
        user3 = null;
        user4 = null;
        user5 = null;
        lobby = null;
        blancLobby = null;
        invite1 = null;
        invite2 = null;
        invite3 = null;
        invite4 = null;
        invite5 = null;
        activationRequest = null;
        changeUsersListRequest = null;
        updateLobbyRequest = null;
    }

    @Test
    void getUserLobby_Success_LobbyFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.getUserCurrentLobby(owner)).thenReturn(lobby);

        var expectedResponse = new LobbyResponse(lobby);
        var response = service.getUserLobby(auth);

        assertEquals(expectedResponse, response);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).getUserCurrentLobby(owner);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(getterService);
    }

    @Test
    void getUserLobby_Throws_UserNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> service.getUserLobby(auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(getterService);
    }

    @Test
    void activateLobby_Success_UsersAreInPool() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.findOrCreateUserLobby(owner)).thenReturn(lobby);
        when(lobbyNotificationsService.subscribeLobbySubjectInPool(
                lobby,
                List.of(user1.getId(), user2.getId(), user3.getId(), lobby.getOwner().getId()))
        )
                .thenReturn(List.of(user1.getId(), user2.getId(), user3.getId(), lobby.getOwner().getId()));
        when(repository.save(any(Lobby.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(lobby.getType()).isEqualTo(LobbyType.INACTIVE);

        var result = service.activateLobby(activationRequest, auth);
        var expectedResult = new LobbyResponse(lobby);

        assertThat(lobby.getType()).isEqualTo(LobbyType.ACTIVE);

        assertEquals(expectedResult, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).findOrCreateUserLobby(owner);
        verify(repository, times(1)).save(any(Lobby.class));
        verify(inviteService, times(1)).setInvitesDelivered(anyList(), anyList());
    }

    @Test
    void activateLobby_Success_UsersAreNotInPool() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.findOrCreateUserLobby(owner)).thenReturn(lobby);
        when(lobbyNotificationsService.subscribeLobbySubjectInPool(
                lobby,
                List.of(user1.getId(), user2.getId(), user3.getId(), lobby.getOwner().getId()))
        )
                .thenReturn(List.of());
        when(repository.save(any(Lobby.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(lobby.getType()).isEqualTo(LobbyType.INACTIVE);

        var result = service.activateLobby(activationRequest, auth);
        var expectedResult = new LobbyResponse(lobby);

        assertThat(lobby.getType()).isEqualTo(LobbyType.ACTIVE);

        assertEquals(expectedResult, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).findOrCreateUserLobby(owner);
        verify(repository, times(1)).save(any(Lobby.class));
        verify(inviteService, times(1)).setInvitesDelivered(anyList(), anyList());
    }

    @Test
    void activateLobby_Success_BlancLobbyActivated() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.findOrCreateUserLobby(owner)).thenReturn(blancLobby);
        when(lobbyNotificationsService.subscribeLobbySubjectInPool(
                blancLobby,
                List.of(blancLobby.getOwner().getId()))
        )
                .thenReturn(List.of(blancLobby.getOwner().getId()));
        when(repository.save(any(Lobby.class))).thenAnswer(i -> i.getArgument(0));

        assertThat(blancLobby.getType()).isEqualTo(LobbyType.INACTIVE);

        var result = service.activateLobby(activationRequest, auth);
        var expectedResult = new LobbyResponse(blancLobby);

        assertThat(blancLobby.getType()).isEqualTo(LobbyType.ACTIVE);
        assertThat(result.users().size()).isEqualTo(0);

        assertEquals(expectedResult, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).findOrCreateUserLobby(owner);
        verify(repository, times(1)).save(any(Lobby.class));
        verify(inviteService, times(1)).setInvitesDelivered(anyList(), anyList());
    }

    @Test
    void activateLobby_Throws_LobbyAlreadyActive() {
        lobby.setType(LobbyType.ACTIVE);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.findOrCreateUserLobby(owner)).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.activateLobby(activationRequest, auth));
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).findOrCreateUserLobby(owner);
        verifyNoInteractions(repository);
        verifyNoInteractions(inviteService);
    }

    @Test
    void activateLobby_Throws_ByNotificationService() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(getterService.findOrCreateUserLobby(owner)).thenReturn(lobby);
        when(lobbyNotificationsService.subscribeLobbySubjectInPool(
                lobby,
                List.of(user1.getId(), user2.getId(), user3.getId()))
        )
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.activateLobby(activationRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(getterService, times(1)).findOrCreateUserLobby(owner);
        verify(repository, times(1)).save(any(Lobby.class));
        verifyNoInteractions(inviteService);
    }

    @Test
    void inviteNewUsers_Success() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.getInvites().addAll(List.of(invite4, invite5));

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.addUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);
        when(lobbyNotificationsService.subscribeNotifyRecipients(
                changedLobby,
                changeUsersListRequest.usersIds())
        )
                .thenReturn(changeUsersListRequest.usersIds());

        var expectedResult = new LobbyResponse(changedLobby);
        var result = service.inviteNewUsers(changeUsersListRequest, auth);

        assertEquals(expectedResult, result);
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).addUsers(changeUsersListRequest, auth);
        verify(lobbyNotificationsService, times(1))
                .subscribeNotifyRecipients(changedLobby, changeUsersListRequest.usersIds());
        verify(inviteService, times(1)).setInvitesDelivered(anyList(), anyList());
        verifyNoInteractions(repository);
    }

    @Test
    void inviteNewUsers_Throws_LobbyIsInactive() {
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.inviteNewUsers(changeUsersListRequest, auth));

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verifyNoInteractions(lobbyUserService);
        verifyNoInteractions(lobbyNotificationsService);
        verifyNoInteractions(inviteService);
        verifyNoInteractions(repository);
    }

    @Test
    void inviteNewUsers_Throws_ByLobbyNotificationService() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.getInvites().addAll(List.of(invite4, invite5));

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.addUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);
        when(lobbyNotificationsService.subscribeNotifyRecipients(
                changedLobby,
                changeUsersListRequest.usersIds())
        )
                .thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> service.inviteNewUsers(changeUsersListRequest, auth));

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).addUsers(changeUsersListRequest, auth);
        verify(lobbyNotificationsService, times(1))
                .subscribeNotifyRecipients(changedLobby, changeUsersListRequest.usersIds());
        verifyNoInteractions(inviteService);
        verifyNoInteractions(repository);
    }

    @Test
    void leaveLobby_Success() {
        lobby.setType(LobbyType.ACTIVE);
        blancLobby.setType(LobbyType.ACTIVE);
        blancLobby.setTitle("User1 blanc lobby");
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(user1);
        when(repository.save(lobby)).thenReturn(lobby);
        when(getterService.getUserCurrentLobby(user1)).thenReturn(blancLobby);

        assertEquals(lobby.getInvites().size(), 3);
        assertTrue(lobby.getInvites().contains(invite1));

        var expectedResult = new LobbyResponse(blancLobby);
        var result = service.leaveLobby(lobby.getId(), auth);

        assertEquals(expectedResult, result);
        assertEquals(lobby.getInvites().size(), 2);
        assertFalse(lobby.getInvites().contains(invite1));
    }

    @Test
    void leaveLobby_Throws_LobbyIsInactive() {
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.leaveLobby(lobby.getId(), auth));
        assertEquals(lobby.getInvites().size(), 3);
        assertTrue(lobby.getInvites().contains(invite1));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verifyNoInteractions(lobbyUserService);
        verifyNoInteractions(userService);
        verifyNoInteractions(lobbyNotificationsService);
        verifyNoInteractions(repository);
        verifyNoInteractions(inviteService);
    }

    @Test
    void leaveLobby_Throws_UserIsNotInLobby() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(user4);

        assertThrows(IllegalArgumentException.class, () -> service.leaveLobby(lobby.getId(), auth));
        assertEquals(lobby.getInvites().size(), 3);
        assertTrue(lobby.getInvites().contains(invite1));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoInteractions(lobbyNotificationsService);
        verifyNoInteractions(repository);
        verifyNoInteractions(inviteService);
    }

    @Test
    void kickUsers_Success() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.getInvites().removeAll(List.of(invite1, invite3));
        changeUsersListRequest = new ChangeUsersListRequest(
                newInviteMessage,
                List.of(user1.getId(), user3.getId())
        );

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.removeUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);

        var result = service.kickUsers(changeUsersListRequest, auth);
        var expectedResult = new LobbyResponse(changedLobby);

        assertEquals(expectedResult, result);

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).removeUsers(changeUsersListRequest, auth);
        verify(lobbyNotificationsService, times(1))
                .unsubscribeNotifyRecipients(changedLobby, changeUsersListRequest);
        verifyNoInteractions(repository);
    }

    @Test
    void kickUsers_Throws_LobbyIsInactive() {
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.kickUsers(changeUsersListRequest, auth));

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verifyNoInteractions(lobbyUserService);
        verifyNoInteractions(lobbyNotificationsService);
        verifyNoInteractions(repository);
    }

    @Test
    void kickUsers_Throws_LobbyNotificationService() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.getInvites().removeAll(List.of(invite1, invite3));
        changeUsersListRequest = new ChangeUsersListRequest(
                newInviteMessage,
                List.of(user1.getId(), user3.getId())
        );

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.removeUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);
        doThrow(RuntimeException.class).when(lobbyNotificationsService)
                .unsubscribeNotifyRecipients(changedLobby, changeUsersListRequest);

        assertThrows(RuntimeException.class, () -> service.kickUsers(changeUsersListRequest, auth));

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).removeUsers(changeUsersListRequest, auth);
        verify(lobbyNotificationsService, times(1))
                .unsubscribeNotifyRecipients(changedLobby, changeUsersListRequest);
        verifyNoInteractions(repository);
    }

    @Test
    void removeUsers_Success() {
        lobby.getInvites().addAll(List.of(invite4, invite5));
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.removeUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);

        var result = service.removeUsers(changeUsersListRequest, auth);
        var expectedResult = new LobbyResponse(changedLobby);

        assertEquals(expectedResult, result);
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).removeUsers(changeUsersListRequest, auth);
    }

    @Test
    void removeUsers_Throws_LobbyIsActive() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.removeUsers(changeUsersListRequest, auth));
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verifyNoInteractions(lobbyUserService);
    }

    @Test
    void addUsers_Success() {
        changedLobby.getInvites().addAll(List.of(invite4, invite5));
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(lobbyUserService.addUsers(changeUsersListRequest, auth)).thenReturn(changedLobby);

        var result = service.addUsers(changeUsersListRequest, auth);
        var expectedResult = new LobbyResponse(changedLobby);

        assertEquals(expectedResult, result);
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(1)).addUsers(changeUsersListRequest, auth);
    }

    @Test
    void addUsers_Throws_LobbyIsActive() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        assertThrows(RestException.class, () -> service.addUsers(changeUsersListRequest, auth));

        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(lobbyUserService, times(0)).addUsers(changeUsersListRequest, auth);
    }

    @Test
    void updateLobbyTitleAndTime_Success_InactiveLoby() {
        changedLobby.setTitle(newTitle);
        changedLobby.setTime(timeStringToOffsetTime(newTime));

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(repository.save(lobby)).thenReturn(changedLobby);

        var expectedResult = new LobbyResponse(changedLobby);
        var result = service.updateLobbyTitleAndTime(updateLobbyRequest, auth);

        assertEquals(expectedResult, result);
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(baseUpdateService, times(1)).setNewValues(updateLobbyRequest, lobby);
        verify(repository, times(1)).save(lobby);
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateLobbyTitleAndTime_Success_ActiveLobby() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.setTitle(newTitle);
        changedLobby.setTime(timeStringToOffsetTime(newTime));

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(repository.save(lobby)).thenReturn(changedLobby);

        var expectedResult = new LobbyResponse(changedLobby);
        var result = service.updateLobbyTitleAndTime(updateLobbyRequest, auth);

        assertEquals(expectedResult, result);
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(baseUpdateService, times(1)).setNewValues(updateLobbyRequest, lobby);
        verify(repository, times(1)).save(lobby);
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));
    }

    @Test
    void updateLobbyTitleAndTime_Throws_ByNotification() {
        lobby.setType(LobbyType.ACTIVE);
        changedLobby.setType(LobbyType.ACTIVE);
        changedLobby.setTitle(newTitle);
        changedLobby.setTime(timeStringToOffsetTime(newTime));

        when(getterService.loadLobbyByAuth(auth)).thenReturn(lobby);
        when(repository.save(lobby)).thenReturn(changedLobby);
        doThrow(RuntimeException.class).when(notificationService).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));

        assertThrows(RuntimeException.class, () -> service.updateLobbyTitleAndTime(updateLobbyRequest, auth));
        verify(getterService, times(1)).loadLobbyByAuth(auth);
        verify(baseUpdateService, times(1)).setNewValues(updateLobbyRequest, lobby);
        verify(repository, times(1)).save(lobby);
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));
    }

    @Test
    void deActivateLobby_Success() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.save(lobby)).thenReturn(lobby);

        var result = service.deactivateLobby(lobby.getId(), auth);

        assertEquals("INACTIVE", result.lobbyType());
        result.users().forEach(user -> assertEquals("INACTIVE", user.inviteState()));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(baseUpdateService, times(1)).isLobbyOwner(lobby, owner.getId());
        verify(repository, times(1)).save(lobby);
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));
        verify(lobbyNotificationsService, times(1)).removeLobbySubject(lobby.getId());
    }

    @Test
    void deActivateLobby_Throws_LobbyIsInactive() {
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);

        assertThrows(RestException.class, () -> service.deactivateLobby(lobby.getId(), auth));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verifyNoInteractions(userService);
        verifyNoInteractions(baseUpdateService);
        verifyNoInteractions(repository);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(lobbyNotificationsService);
    }

    @Test
    void deActivateLobby_Throws_AuthenticatedUserIsNotTheLobbyOwner() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        doThrow(IllegalArgumentException.class).when(baseUpdateService).isLobbyOwner(lobby, owner.getId());

        assertThrows(IllegalArgumentException.class, () -> service.deactivateLobby(lobby.getId(), auth));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(baseUpdateService, times(1)).isLobbyOwner(lobby, owner.getId());
        verifyNoInteractions(repository);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(lobbyNotificationsService);
    }

    @Test
    void deActivateLobby_Throws_ByNotifyingMembers() {
        lobby.setType(LobbyType.ACTIVE);
        when(getterService.getLobbyByIdOrThrow(lobby.getId())).thenReturn(lobby);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.save(lobby)).thenReturn(lobby);
        doThrow(RuntimeException.class).when(notificationService).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));

        assertThrows(RuntimeException.class, () -> service.deactivateLobby(lobby.getId(), auth));

        verify(getterService, times(1)).getLobbyByIdOrThrow(lobby.getId());
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(baseUpdateService, times(1)).isLobbyOwner(lobby, owner.getId());
        verify(repository, times(1)).save(lobby);
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), anyLong(), any(NotificationData.class));
        verifyNoInteractions(lobbyNotificationsService);
    }
}