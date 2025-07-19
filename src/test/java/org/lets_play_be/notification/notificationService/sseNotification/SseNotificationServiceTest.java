package org.lets_play_be.notification.notificationService.sseNotification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.MessageNotificationData;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.OffsetTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SseNotificationServiceTest {

    @Mock
    SseService sseServiceMock;
    @Mock
    SseLiveRecipientPool recipientPoolMock;
    @Mock
    LobbySubjectPool subjectPoolMock;
    @Mock
    AppUserService appUserServiceMock;
    @InjectMocks
    SseNotificationService sseNotificationService;

    AppUser appUserMock;
    Authentication authMock;
    SseEmitter emitterMock;
    LobbyActive lobbyActiveMock;
    SseNotificationObserver observerMock;
    LobbySubject lobbySubjectMock;
    MessageNotificationData notificationData;

    @BeforeEach
    void setUp() {
        appUserMock = new AppUser(1L, "", "email@email.com", "password", "");

        authMock = new UsernamePasswordAuthenticationToken(appUserMock.getEmail(), appUserMock.getPassword());

        emitterMock = new SseEmitter();

        lobbyActiveMock = new LobbyActive(1L, "", OffsetTime.now().plusHours(1), appUserMock);

        lobbySubjectMock = new LobbySubject(lobbyActiveMock.getId());

        notificationData = new MessageNotificationData("message");

        observerMock = new SseNotificationObserver(emitterMock);
    }

    @AfterEach
    void tearDown() {
        appUserMock = null;
        authMock = null;
        emitterMock = null;
        observerMock = null;
        lobbyActiveMock = null;
        lobbySubjectMock = null;
        notificationData = null;
    }

    @Test
    void subscribeForSse_Success() {
        when(appUserServiceMock.getUserByEmailOrThrow(authMock.getName())).thenReturn(appUserMock);
        when(sseServiceMock.createSseConnection()).thenReturn(emitterMock);
        doNothing().when(recipientPoolMock).addObserver(appUserMock.getId(), emitterMock);

        SseEmitter result = sseNotificationService.subscribeForSse(authMock);

        assertThat(emitterMock).isEqualTo(result);

        verify(appUserServiceMock, times(1)).getUserByEmailOrThrow(authMock.getName());
        verify(sseServiceMock, times(1)).createSseConnection();
        verify(recipientPoolMock, times(1)).addObserver(appUserMock.getId(), emitterMock);
    }

    @Test
    void subscribeForSse_User_Not_Found() {
        when(appUserServiceMock.getUserByEmailOrThrow(authMock.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> sseNotificationService.subscribeForSse(authMock));

        verify(appUserServiceMock, times(1)).getUserByEmailOrThrow(authMock.getName());
        verify(sseServiceMock, times(0)).createSseConnection();
        verify(recipientPoolMock, times(0)).addObserver(appUserMock.getId(), emitterMock);
    }

    @Test
    void subscribeForSse_Sse_Connection_Didnt_Created() {
        when(appUserServiceMock.getUserByEmailOrThrow(authMock.getName())).thenReturn(appUserMock);
        when(sseServiceMock.createSseConnection()).thenThrow(RestException.class);

        assertThrows(RestException.class, () -> sseNotificationService.subscribeForSse(authMock));

        verify(appUserServiceMock, times(1)).getUserByEmailOrThrow(authMock.getName());
        verify(sseServiceMock, times(1)).createSseConnection();
        verify(recipientPoolMock, times(0)).addObserver(appUserMock.getId(), emitterMock);
    }

    @Test
    void subscribeSseObserverForActiveLobby_Success() {
        when(recipientPoolMock.isInPool(appUserMock.getId())).thenReturn(true);
        when(recipientPoolMock.getObserver(appUserMock.getId())).thenReturn(observerMock);
        when(subjectPoolMock.getSubject(lobbyActiveMock.getId())).thenReturn(lobbySubjectMock);

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(0);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        sseNotificationService.subscribeSseObserverForActiveLobby(appUserMock.getId(), lobbyActiveMock.getId());

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(1);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(1);

        verify(recipientPoolMock, times(1)).isInPool(appUserMock.getId());
        verify(recipientPoolMock, times(1)).getObserver(appUserMock.getId());
        verify(subjectPoolMock, times(1)).getSubject(lobbyActiveMock.getId());
    }

    @Test
    void subscribeSseObserverForActiveLobby_User_Not_InPool() {
        when(recipientPoolMock.isInPool(appUserMock.getId())).thenReturn(false);

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(0);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        sseNotificationService.subscribeSseObserverForActiveLobby(appUserMock.getId(), lobbyActiveMock.getId());

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(0);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        verify(recipientPoolMock, times(1)).isInPool(appUserMock.getId());
        verify(recipientPoolMock, times(0)).getObserver(appUserMock.getId());
        verify(subjectPoolMock, times(0)).getSubject(lobbyActiveMock.getId());
    }

    @Test
    void subscribeSseObserverForActiveLobby_No_Subject_InPool() {
        when(recipientPoolMock.isInPool(appUserMock.getId())).thenReturn(true);
        when(recipientPoolMock.getObserver(appUserMock.getId())).thenReturn(observerMock);
        when(subjectPoolMock.getSubject(lobbyActiveMock.getId())).thenReturn(null);

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(0);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        assertThrowsExactly(RestException.class,
                () -> sseNotificationService.subscribeSseObserverForActiveLobby(appUserMock.getId(), lobbyActiveMock.getId()),
                "Subscription for Lobby failed");

        assertThat(observerMock.getOnCloseCallbacks().size()).isEqualTo(0);
        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        verify(recipientPoolMock, times(1)).isInPool(appUserMock.getId());
        verify(recipientPoolMock, times(1)).getObserver(appUserMock.getId());
        verify(subjectPoolMock, times(1)).getSubject(lobbyActiveMock.getId());
    }

    @Test
    void notifyLobbyMembers_Success() {
        when(subjectPoolMock.getSubject(lobbyActiveMock.getId())).thenReturn(lobbySubjectMock);

        sseNotificationService.notifyLobbyMembers(lobbyActiveMock.getId(), notificationData);

        verify(subjectPoolMock, times(1)).getSubject(lobbyActiveMock.getId());
    }


    @Test
    void notifyLobbyMembers_No_SubjectFound() {
        when(subjectPoolMock.getSubject(lobbyActiveMock.getId())).thenReturn(null);

        assertThrowsExactly(RestException.class,
                () -> sseNotificationService.notifyLobbyMembers(lobbyActiveMock.getId(),notificationData),
                "Members Notification failed");

        verify(subjectPoolMock, times(1)).getSubject(lobbyActiveMock.getId());
    }

    @Test
    void unsubscribeUserFromSubject_Success() {

        observerMock.addOnCloseCallback(lobbyActiveMock.getId(), ()->lobbySubjectMock.unsubscribe(observerMock));

        when(recipientPoolMock.isInPool(appUserMock.getId())).thenReturn(true);
        when(recipientPoolMock.getObserver(appUserMock.getId())).thenReturn(observerMock);

        lobbySubjectMock.getObservers().add(observerMock);

        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(1);
        assertThat(lobbySubjectMock.getObservers()).contains(observerMock);

        sseNotificationService.unsubscribeUserFromSubject(appUserMock.getId(), lobbyActiveMock.getId());

        assertThat(lobbySubjectMock.getObservers().size()).isEqualTo(0);

        verify(recipientPoolMock, times(1)).isInPool(appUserMock.getId());
        verify(recipientPoolMock, times(1)).getObserver(appUserMock.getId());
    }

    @Test
    void unsubscribeUserFromSubject_Recipient_Not_inPool() {

        when(recipientPoolMock.isInPool(appUserMock.getId())).thenReturn(false);

        sseNotificationService.unsubscribeUserFromSubject(appUserMock.getId(), lobbyActiveMock.getId());

        verify(recipientPoolMock, times(1)).isInPool(appUserMock.getId());
        verify(recipientPoolMock, times(0)).getObserver(appUserMock.getId());
    }
}