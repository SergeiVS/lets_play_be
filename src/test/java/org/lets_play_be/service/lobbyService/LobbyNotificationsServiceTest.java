package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lets_play_be.dto.lobbyDto.ChangeUsersListRequest;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.dto.NotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationObserver;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.OffsetTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyNotificationsServiceTest {
    @Mock
    private LobbySubjectPool subjectPool;

    @Mock
    private SseLiveRecipientPool recipientPool;

    @Mock
    private SseNotificationService sseNotificationService;

    @Mock
    private AppUserService userService;

    @Mock
    private LobbyGetterService lobbyGetterService;

    @InjectMocks
    LobbyNotificationsService lobbyNotificationsService;

    private AppUser owner;
    private AppUser user1;
    private AppUser user2;

    private Lobby lobby;
    private LobbySubject lobbySubject;

    private ChangeUsersListRequest request;

    @BeforeEach
    void setUp() {
        owner = new AppUser(1L, "Owner", "", "", "");
        user1 = new AppUser(1L, "User1", "", "", "");
        user2 = new AppUser(2L, "User2", "", "", "");

        lobby = new Lobby(1L, "", OffsetTime.now(), owner);
        lobbySubject = new LobbySubject(lobby.getId());

        request = new ChangeUsersListRequest("Message", List.of(user1.getId(), user2.getId()));
    }

    @AfterEach
    void tearDown() {
        owner = null;
        user1 = null;
        user2 = null;
        lobby = null;
        lobbySubject = null;
        request = null;
    }

    @Test
    void subscribeNotifyRecipients_Success_RecipientsAreInPool() {
        when(recipientPool.isInPool(anyLong())).thenReturn(true);

        lobbyNotificationsService.subscribeNotifyRecipients(lobby, List.of(user1.getId(), user2.getId()));

        verify(recipientPool, times(2))
                .isInPool(anyLong());
        verify(sseNotificationService, times(2))
                .subscribeSseObserverToLobby(anyLong(), anyLong());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(recipientPool);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void subscribeNotifyRecipients_Success_RecipientsNotInPool() {
        when(recipientPool.isInPool(anyLong())).thenReturn(false);

        lobbyNotificationsService.subscribeNotifyRecipients(lobby, List.of(user1.getId(), user2.getId()));

        verify(recipientPool, times(2))
                .isInPool(anyLong());
        verify(sseNotificationService, times(1)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(recipientPool);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void unsubscribeNotifyRecipients_Success_RecipientsAreInPool() {
        when(subjectPool.getSubject(lobby.getId())).thenReturn(lobbySubject);
        when(recipientPool.isInPool(anyLong())).thenReturn(true);
        when(userService.getUsersListByIds(List.of(user1.getId(), user2.getId()))).thenReturn(List.of(user1, user2));
        when(lobbyGetterService.getUserCurrentLobby(any(AppUser.class))).thenReturn(lobby);
        when(recipientPool.getObserver(anyLong())).thenReturn(new SseNotificationObserver(new SseEmitter()));

        lobbyNotificationsService.unsubscribeNotifyRecipients(lobby, request);

        var numberOfKickedUsers = request.usersIds().size();

        verify(subjectPool, times(1))
                .getSubject(lobby.getId());
        verify(recipientPool, times(numberOfKickedUsers))
                .isInPool(anyLong());
        verify(recipientPool, times(numberOfKickedUsers))
                .getObserver(anyLong());
        verify(userService, times(1))
                .getUsersListByIds(anyList());
        verify(lobbyGetterService, times(numberOfKickedUsers))
                .getUserCurrentLobby(any(AppUser.class));
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
    }

    @Test
    void unsubscribeNotifyRecipients_Success_RecipientsNotInPool() {
        when(subjectPool.getSubject(lobby.getId())).thenReturn(lobbySubject);
        when(recipientPool.isInPool(anyLong())).thenReturn(false);
        when(userService.getUsersListByIds(List.of(user1.getId(), user2.getId()))).thenReturn(List.of(user1, user2));

        lobbyNotificationsService.unsubscribeNotifyRecipients(lobby, request);

        var numberOfKickedUsers = request.usersIds().size();

        verify(subjectPool, times(1))
                .getSubject(lobby.getId());
        verify(recipientPool, times(numberOfKickedUsers))
                .isInPool(anyLong());
        verify(userService, times(1))
                .getUsersListByIds(anyList());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(recipientPool);
        verifyNoInteractions(lobbyGetterService);
    }

    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void unsubscribeNotifyRecipients_Throws_UserIdsInvalid(Class<? extends Exception> ex) {
        when(userService.getUsersListByIds(anyList())).thenThrow(ex);

        assertThrows(ex, () -> lobbyNotificationsService.unsubscribeNotifyRecipients(lobby, request));


        verify(userService, times(1))
                .getUsersListByIds(anyList());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(sseNotificationService);
        verifyNoInteractions(subjectPool);
        verifyNoInteractions(recipientPool);
        verifyNoInteractions(lobbyGetterService);
    }

    @Test
    void unsubscribeNotifyRecipients_Trows_ByMembersNotification() {
        when(subjectPool.getSubject(lobby.getId())).thenReturn(lobbySubject);
        when(recipientPool.isInPool(anyLong())).thenReturn(false);
        when(userService.getUsersListByIds(List.of(user1.getId(), user2.getId()))).thenReturn(List.of(user1, user2));
        doThrow(RuntimeException.class).when(sseNotificationService).notifyLobbyMembers(anyLong(), any(NotificationData.class));

        var numberOfKickedUsers = request.usersIds().size();

        assertThrows(RuntimeException.class, () -> lobbyNotificationsService.unsubscribeNotifyRecipients(lobby, request));

        verify(subjectPool, times(1))
                .getSubject(lobby.getId());
        verify(recipientPool, times(numberOfKickedUsers))
                .isInPool(anyLong());
        verify(userService, times(1))
                .getUsersListByIds(anyList());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(recipientPool);
        verifyNoInteractions(lobbyGetterService);
    }

    @Test
    void notifyInvitedUsers_Success() {
        doNothing().when(sseNotificationService)
                .notifyLobbyMembers(anyLong(), any());

        lobbyNotificationsService
                .notifyInvitedUsers(lobby, new MessageNotificationData("message"));

        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void notifyInvitedUsers_Throws_ByNotifyingMembers() {
        doThrow(RuntimeException.class)
                .when(sseNotificationService).notifyLobbyMembers(anyLong(), any());

        assertThrows(RuntimeException.class,
                () -> lobbyNotificationsService.notifyInvitedUsers(lobby, new MessageNotificationData("message"))
        );

        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verifyNoMoreInteractions(sseNotificationService);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void subscribeLobbySubjectInPool_Success_MembersAreInPool() {
        doNothing().when(subjectPool).addSubject(any(LobbySubject.class));
        when(recipientPool.isInPool(anyLong())).thenReturn(true);
        doNothing().when(sseNotificationService).notifyLobbyMembers(anyLong(), any());

        var numberOfUsersInList = request.usersIds().size();

        var result = lobbyNotificationsService
                .subscribeLobbySubjectInPool(lobby, request.usersIds());
        System.out.println(request.usersIds());

        result.forEach(id->assertTrue(request.usersIds().contains(id)));


        verify(subjectPool, times(1))
                .addSubject(any(LobbySubject.class));
        verify(recipientPool, times(numberOfUsersInList))
                .isInPool(anyLong());
        verify(sseNotificationService, times(numberOfUsersInList))
                .subscribeSseObserverToLobby(anyLong(),anyLong());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any());
        verifyNoMoreInteractions(subjectPool);
        verifyNoMoreInteractions(recipientPool);

        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void subscribeLobbySubjectInPool_Success_MembersAreNotInPool() {
        doNothing().when(subjectPool).addSubject(any(LobbySubject.class));
        when(recipientPool.isInPool(anyLong())).thenReturn(false);
        doNothing().when(sseNotificationService).notifyLobbyMembers(anyLong(), any());

        var numberOfUsersInList = request.usersIds().size();

        var result = lobbyNotificationsService
                .subscribeLobbySubjectInPool(lobby, request.usersIds());

        assertNotEquals(request.usersIds(), result);
        verify(subjectPool, times(1))
                .addSubject(any(LobbySubject.class));
        verify(recipientPool, times(numberOfUsersInList))
                .isInPool(anyLong());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any());
        verifyNoMoreInteractions(subjectPool);
        verifyNoMoreInteractions(recipientPool);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void subscribeLobbySubjectInPoo_Throws_ByNotifyingMembers() {
        doNothing().when(subjectPool).addSubject(any(LobbySubject.class));
        when(recipientPool.isInPool(anyLong())).thenReturn(false);
        doThrow(RuntimeException.class).when(sseNotificationService).notifyLobbyMembers(anyLong(), any());

        var numberOfUsersInList = request.usersIds().size();

        assertThrows(RuntimeException.class, () -> lobbyNotificationsService
                .subscribeLobbySubjectInPool(lobby, request.usersIds()));

        verify(subjectPool, times(1))
                .addSubject(any(LobbySubject.class));
        verify(recipientPool, times(numberOfUsersInList))
                .isInPool(anyLong());
        verify(sseNotificationService, times(1))
                .notifyLobbyMembers(anyLong(), any());
        verifyNoMoreInteractions(subjectPool);
        verifyNoMoreInteractions(recipientPool);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void subscribeLobbySubjectInPoo_Throws_BySubscribingToSubject() {
        doNothing().when(subjectPool).addSubject(any(LobbySubject.class));
        when(recipientPool.isInPool(anyLong())).thenReturn(true);
        doThrow(RuntimeException.class).when(sseNotificationService).subscribeSseObserverToLobby(anyLong(), anyLong());

        assertThrows(RuntimeException.class, () -> lobbyNotificationsService
                .subscribeLobbySubjectInPool(lobby, request.usersIds()));

        verify(subjectPool, times(1))
                .addSubject(any(LobbySubject.class));
        verify(recipientPool, times(1))
                .isInPool(anyLong());
        verify(sseNotificationService, times(1))
                .subscribeSseObserverToLobby(anyLong(), anyLong());
        verifyNoMoreInteractions(subjectPool);
        verifyNoMoreInteractions(recipientPool);
        verifyNoMoreInteractions(sseNotificationService);
    }

    @Test
    void removeLobbySubject() {
        lobbyNotificationsService.removeLobbySubject(lobby.getId());
        verify(subjectPool, times(1)).removeSubject(anyLong());
        verifyNoMoreInteractions(subjectPool);
    }
}