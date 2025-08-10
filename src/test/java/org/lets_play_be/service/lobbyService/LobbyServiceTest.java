package org.lets_play_be.service.lobbyService;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.dto.*;
import org.lets_play_be.notification.notificationService.LobbySubject;
import org.lets_play_be.notification.notificationService.LobbySubjectPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseLiveRecipientPool;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationObserver;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    @Mock
    LobbyActiveRepository repository;
    @Mock
    LobbyBaseUpdateService baseUpdateService;
    @Mock
    AppUserService userService;
    @Mock
    InviteService inviteService;
    @Mock
    SseNotificationService notificationService;
    @Mock
    SseLiveRecipientPool recipientPool;
    @Mock
    LobbySubjectPool subjectPool;
    @Mock
    LobbyPresetService presetService;

    @InjectMocks
    LobbyActiveService lobbyActiveService;

    AppUser owner;
    AppUser user1;
    AppUser user2;
    AppUser user3;
    AppUser user4;
    AppUser user5;

    Authentication auth;
    Authentication authUser1;
    UserAvailability ownerAvailability;
    UserAvailability userAvailability1;
    UserAvailability userAvailability2;
    UserAvailability userAvailability3;
    UserAvailability userAvailability4;
    UserAvailability userAvailability5;

    NewActiveLobbyRequest newLobbyRequest;
    ActiveLobbyResponse lobbyResponse;
    UpdateLobbyRequest updateTitleTimeRequest;
    InviteOrKickUsersRequest inviteUsersRequest;
    ActivatePresetRequest activationRequest;

    Invite invite1;
    Invite invite2;
    Invite invite3;
    Invite invite4;
    Invite invite5;

    Lobby lobbyForSave;
    Lobby savedLobby;
    LobbyPreset preset;
    LobbyPreset presetUser1;

    @BeforeEach
    void setUp() {

        String message = "Message";
        String newInviteMessage = "New invite Message";
        String title = "Title";
        String timeString = "18:00:00+00:00";

        owner = new AppUser(10L, "Name", "email@email.com", "password", "url");
        user1 = new AppUser(11L, "Name1", "email@email.com", "password1", "url1");
        user2 = new AppUser(12L, "Name2", "email2@email.com", "password2", "url2");
        user3 = new AppUser(13L, "Name3", "email3@email.com", "password3", "url3");
        user4 = new AppUser(14L, "User4", "email4@email.com", "password4", "Url4");
        user5 = new AppUser(15L, "User5", "email5@email.com", "password5", "Url5");

        userAvailability4 = new UserAvailability(4L, AvailabilityEnum.AVAILABLE);
        userAvailability5 = new UserAvailability(5L, AvailabilityEnum.AVAILABLE);

        user4.setAvailability(userAvailability4);
        user5.setAvailability(userAvailability5);

        ownerAvailability = new UserAvailability(10, AvailabilityEnum.AVAILABLE);
        userAvailability1 = new UserAvailability(11, AvailabilityEnum.AVAILABLE);
        userAvailability2 = new UserAvailability(12, AvailabilityEnum.UNAVAILABLE);
        userAvailability3 = new UserAvailability(13, AvailabilityEnum.TEMPORARILY_UNAVAILABLE);
        userAvailability3.setUnavailableFrom(OffsetTime.now().plusHours(1));
        userAvailability1.setUnavailableFrom(OffsetTime.now().plusHours(3));
        owner.setAvailability(ownerAvailability);
        user1.setAvailability(userAvailability1);
        user2.setAvailability(userAvailability2);
        user3.setAvailability(userAvailability3);

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), owner.getPassword());
        authUser1 = new UsernamePasswordAuthenticationToken(user1.getEmail(), user1.getPassword());

        lobbyForSave = new Lobby(title, OffsetTime.parse(timeString), owner);

        preset = new LobbyPreset(1L, title, OffsetTime.parse(timeString), owner);
        preset.getUsers().addAll(List.of(user1, user2, user3));
        presetUser1 = new LobbyPreset(2L, title, OffsetTime.parse(timeString), user1);

        invite1 = new Invite(1L, user1, lobbyForSave, message);
        invite2 = new Invite(2L, user2, lobbyForSave, message);
        invite3 = new Invite(3L, user3, lobbyForSave, message);
        invite4 = new Invite(4L, user4, lobbyForSave, newInviteMessage);
        invite5 = new Invite(5L, user5, lobbyForSave, newInviteMessage);

        savedLobby = new Lobby(1L, "Title", OffsetTime.parse(timeString), owner);
        savedLobby.getInvites().addAll(List.of(invite1, invite2, invite3));

        var newTitle = "newTitle";
        var newTime = "20:00:00+02:00";
        updateTitleTimeRequest = new UpdateLobbyRequest(savedLobby.getId(), newTitle, newTime);
        newLobbyRequest = new NewActiveLobbyRequest(title, timeString, message, List.of(11L, 12L, 13L));
        activationRequest = new ActivatePresetRequest(message);
        inviteUsersRequest = new InviteOrKickUsersRequest(newInviteMessage, List.of(user4.getId(), user5.getId()));

        lobbyResponse = new ActiveLobbyResponse(savedLobby);
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
        authUser1 = null;

        ownerAvailability = null;
        userAvailability1 = null;
        userAvailability2 = null;
        userAvailability3 = null;

        newLobbyRequest = null;
        lobbyResponse = null;
        preset = null;
        presetUser1 = null;

        invite1 = null;
        invite2 = null;
        invite3 = null;
        invite4 = null;
        invite5 = null;

        lobbyForSave = null;
        savedLobby = null;

        updateTitleTimeRequest = null;
        activationRequest = null;
    }

    @Deprecated
    @Test
    void createActiveLobby_Success() {

        var invitedUser1 = new InvitedUserResponse(invite1);
        var invitedUser2 = new InvitedUserResponse(invite2);
        var invitedUser3 = new InvitedUserResponse(invite3);
        var notificationData = new LobbyCreatedNotificationData(savedLobby);
        var usersCount = newLobbyRequest.userIds().size();

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(userService.getUsersListByIds(newLobbyRequest.userIds())).thenReturn(List.of(user1, user2, user3));
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(repository.save(any(Lobby.class))).thenReturn(savedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(true);

        ActiveLobbyResponse result = lobbyActiveService.createActiveLobby(newLobbyRequest, auth);

        assertThat(lobbyResponse).isEqualTo(result);
        Assertions.assertTrue(result.users().containsAll(List.of(invitedUser1, invitedUser2, invitedUser3)));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(subjectPool, times(1)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(usersCount)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(recipientPool, times(usersCount * 2)).isInPool(anyLong());
        verify(inviteService, times(usersCount)).updateIsDelivered(anyLong());
        verify(repository, times(1)).save(any(Lobby.class));
    }

    @Deprecated
    @Test
    void createActiveLobby_Success_No_Users_Online() {

        var invitedUser1 = new InvitedUserResponse(invite1);
        var invitedUser2 = new InvitedUserResponse(invite2);
        var invitedUser3 = new InvitedUserResponse(invite3);
        var notificationData = new LobbyCreatedNotificationData(savedLobby);
        var usersCount = newLobbyRequest.userIds().size();

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(userService.getUsersListByIds(newLobbyRequest.userIds())).thenReturn(List.of(user1, user2, user3));
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(repository.save(any(Lobby.class))).thenReturn(savedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(false);

        ActiveLobbyResponse result = lobbyActiveService.createActiveLobby(newLobbyRequest, auth);

        assertThat(lobbyResponse).isEqualTo(result);
        Assertions.assertTrue(result.users().containsAll(List.of(invitedUser1, invitedUser2, invitedUser3)));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(subjectPool, times(1)).addSubject(any(LobbySubject.class));

        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(recipientPool, times(usersCount * 2)).isInPool(anyLong());
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(repository, times(1)).save(any(Lobby.class));
    }

    @Deprecated
    @Test
    void createActiveLobby_Throws_Owner_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.createActiveLobby(newLobbyRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(0)).existsLobbyActiveByOwner(owner);
        verify(userService, times(0)).getUsersListByIds(anyList());
        verify(subjectPool, times(0)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
        verify(repository, times(0)).save(any(Lobby.class));
    }

    @Deprecated
    @Test
    void createActiveLobby_Throws_Owner_Has_Lobby() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(true);

        assertThrowsExactly(IllegalArgumentException.class,
                () -> lobbyActiveService.createActiveLobby(newLobbyRequest, auth),
                "The Lobby for given owner already exists");

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(userService, times(0)).getUsersListByIds(anyList());
        verify(subjectPool, times(0)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
        verify(repository, times(0)).save(any(Lobby.class));
    }

    @Deprecated
    @Test
    void createActiveLobby_Throws_Request_Contains_Invalid_UserIds() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(userService.getUsersListByIds(anyList())).thenThrow(new UsernameNotFoundException(anyString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.createActiveLobby(newLobbyRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(subjectPool, times(0)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
        verify(repository, times(0)).save(any(Lobby.class));
    }

    @Test
    void createLobbyFromPreset_Success() {
        var invitedUser1 = new InvitedUserResponse(invite1);
        var invitedUser2 = new InvitedUserResponse(invite2);
        var invitedUser3 = new InvitedUserResponse(invite3);
        var notificationData = new LobbyCreatedNotificationData(savedLobby);
        var usersCount = newLobbyRequest.userIds().size();

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(presetService.getPresetByOwnerIdOrThrow(owner.getId())).thenReturn(preset);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(repository.save(any(Lobby.class))).thenReturn(savedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(true);

        ActiveLobbyResponse result = lobbyActiveService.createLobbyFromPreset(activationRequest, auth);

        assertThat(lobbyResponse).isEqualTo(result);
        Assertions.assertTrue(result.users().containsAll(List.of(invitedUser1, invitedUser2, invitedUser3)));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(presetService, times(1)).getPresetByOwnerIdOrThrow(owner.getId());
        verify(subjectPool, times(1)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(usersCount)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(recipientPool, times(usersCount * 2)).isInPool(anyLong());
        verify(inviteService, times(usersCount)).updateIsDelivered(anyLong());
        verify(repository, times(1)).save(any(Lobby.class));
    }

    @Test
    void createLobbyFromPreset_Success_No_Users_Online() {
        var invitedUser1 = new InvitedUserResponse(invite1);
        var invitedUser2 = new InvitedUserResponse(invite2);
        var invitedUser3 = new InvitedUserResponse(invite3);
        var notificationData = new LobbyCreatedNotificationData(savedLobby);
        var usersCount = newLobbyRequest.userIds().size();

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(presetService.getPresetByOwnerIdOrThrow(owner.getId())).thenReturn(preset);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(repository.save(any(Lobby.class))).thenReturn(savedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(false);

        ActiveLobbyResponse result = lobbyActiveService.createLobbyFromPreset(activationRequest, auth);

        assertThat(lobbyResponse).isEqualTo(result);
        Assertions.assertTrue(result.users().containsAll(List.of(invitedUser1, invitedUser2, invitedUser3)));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(presetService, times(1)).getPresetByOwnerIdOrThrow(owner.getId());
        verify(subjectPool, times(1)).addSubject(any(LobbySubject.class));
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(recipientPool, times(usersCount * 2)).isInPool(anyLong());
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
        verify(repository, times(1)).save(any(Lobby.class));
    }

    @Test
    void createLobbyFromPreset_Throws_Owner_Not_Found() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.createLobbyFromPreset(activationRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(repository);
        verifyNoInteractions(recipientPool);
        verifyNoInteractions(inviteService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(subjectPool);
    }

    @Test
    void createLobbyFromPreset_Throws_Owner_Has_Lobby() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.createLobbyFromPreset(activationRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(presetService);
        verifyNoInteractions(recipientPool);
        verifyNoInteractions(inviteService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(subjectPool);
    }

    @Test
    void createLobbyFromPreset_Throws_Preset_Contains_Invalid_UserIds() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.existsLobbyActiveByOwner(owner)).thenReturn(false);
        when(presetService.getPresetByOwnerIdOrThrow(owner.getId())).thenReturn(preset);
        when(userService.getUsersListByIds(anyList())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.createLobbyFromPreset(activationRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).existsLobbyActiveByOwner(owner);
        verify(presetService, times(1)).getPresetByOwnerIdOrThrow(owner.getId());
        verify(userService, times(1)).getUsersListByIds(anyList());
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(presetService);
        verifyNoInteractions(recipientPool);
        verifyNoInteractions(inviteService);
        verifyNoInteractions(notificationService);
        verifyNoInteractions(subjectPool);
    }

    @Test
    void getUsersActiveLobby_Lobby_Found() {
        var expectedResult = new ActiveLobbyResponse(savedLobby);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.of(savedLobby));

        var result = lobbyActiveService.getUsersActiveLobby(auth);

        assertEquals(expectedResult, result);
    }

    @Test
    void getUsersActiveLobby_Lobby_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.empty());

        var result = lobbyActiveService.getUsersActiveLobby(auth);

        assertNull(result);
    }

    @Test
    void getUsersActiveLobby_Throws_Owner_Not_Found() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.getUsersActiveLobby(auth));
    }

    @Test
    void inviteNewUsers_Success() {
        Lobby updatedLobby = cloneLobby(savedLobby);
        updatedLobby.getInvites().addAll(List.of(invite4, invite5));

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.ofNullable(savedLobby));
        when(userService.getUsersListByIds(inviteUsersRequest.usersIds())).thenReturn(List.of(user4, user5));
        when(repository.save(updatedLobby)).thenReturn(updatedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(true);

        ActiveLobbyResponse expectedResult = new ActiveLobbyResponse(updatedLobby);
        ActiveLobbyResponse result = lobbyActiveService.inviteNewUsers(inviteUsersRequest, auth);

        assertEquals(expectedResult, result);

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyActiveByOwnerId(owner.getId());
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(repository, times(1)).save(updatedLobby);
        verify(recipientPool, times(4)).isInPool(anyLong());
        verify(notificationService, times(2)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(inviteService, times(2)).updateIsDelivered(anyLong());
    }

    @Test
    void inviteNewUsers_No_Users_Online_Success() {
        Lobby updatedLobby = cloneLobby(savedLobby);
        updatedLobby.getInvites().addAll(List.of(invite4, invite5));

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.ofNullable(savedLobby));
        when(userService.getUsersListByIds(inviteUsersRequest.usersIds())).thenReturn(List.of(user4, user5));
        when(repository.save(updatedLobby)).thenReturn(updatedLobby);
        when(recipientPool.isInPool(anyLong())).thenReturn(false);

        ActiveLobbyResponse expectedResult = new ActiveLobbyResponse(updatedLobby);
        ActiveLobbyResponse result = lobbyActiveService.inviteNewUsers(inviteUsersRequest, auth);

        assertEquals(expectedResult, result);

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyActiveByOwnerId(owner.getId());
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(repository, times(1)).save(updatedLobby);
        verify(recipientPool, times(4)).isInPool(anyLong());
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
    }

    @Test
    void inviteNewUsers_Throws_No_Lobby_By_Current_User() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.inviteNewUsers(inviteUsersRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(0)).findLobbyActiveByOwnerId(owner.getId());
        verify(userService, times(0)).getUsersListByIds(anyList());
        verify(repository, times(0)).save(any());
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
    }


    @Test
    void inviteNewUsers_Throws_Lobby_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.empty());

        assertThrows(RestException.class, () -> lobbyActiveService.inviteNewUsers(inviteUsersRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyActiveByOwnerId(owner.getId());
        verify(userService, times(0)).getUsersListByIds(anyList());
        verify(repository, times(0)).save(any());
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(notificationService, times(0)).subscribeSseObserverForActiveLobby(anyLong(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
        verify(inviteService, times(0)).updateIsDelivered(anyLong());
    }


    @Test
    void leaveLobby_Success() {
        var updatedLobby = cloneLobby(savedLobby);
        updatedLobby.getInvites().remove(invite1);

        when(userService.getUserByEmailOrThrow(authUser1.getName())).thenReturn(user1);
        when(repository.findById(savedLobby.getId())).thenReturn(Optional.of(savedLobby));
        when(repository.save(updatedLobby)).thenReturn(updatedLobby);
        when(presetService.getPresetFullResponse(user1)).thenReturn(new PresetFullResponse(presetUser1));

        assertThat(savedLobby.getInvites().size()).isEqualTo(3);
        assertTrue(savedLobby.getInvites().contains(invite1));

        var expectedResult = new PresetFullResponse(presetUser1);
        var result = lobbyActiveService.leaveLobby(savedLobby.getId(), authUser1);

        assertEquals(expectedResult, result);
        assertThat(savedLobby.getInvites().size()).isEqualTo(2);
        assertFalse(savedLobby.getInvites().contains(invite1));

        verify(userService, times(1)).getUserByEmailOrThrow(authUser1.getName());
        verify(repository, times(1)).findById(savedLobby.getId());
        verify(repository, times(1)).save(updatedLobby);
        verify(notificationService, times(1))
                .notifyLobbyMembers(savedLobby.getId(), new MessageNotificationData(user1.getName() + " was leaved the lobby: " + savedLobby.getTitle()));
    }

    @Test
    void leaveLobby_Throws_User_Not_Lobby_Member() {
        when(userService.getUserByEmailOrThrow(anyString())).thenReturn(user4);
        when(repository.findById(savedLobby.getId())).thenReturn(Optional.of(savedLobby));

        assertThat(savedLobby.getInvites().size()).isEqualTo(3);
        assertTrue(savedLobby.getInvites().contains(invite1));

        assertThrows(
                IllegalArgumentException.class,
                () -> lobbyActiveService.leaveLobby(savedLobby.getId(), auth),
                "User is not a lobby member"
        );

        assertThat(savedLobby.getInvites().size()).isEqualTo(3);
        assertTrue(savedLobby.getInvites().contains(invite1));

        verify(userService, times(1)).getUserByEmailOrThrow(authUser1.getName());
        verify(repository, times(1)).findById(savedLobby.getId());
        verify(repository, times(0)).save(any());
        verify(notificationService, times(0))
                .notifyLobbyMembers(anyLong(), any(NotificationData.class));
    }

    @Test
    void kickUsers_Success() {
        inviteUsersRequest = new InviteOrKickUsersRequest("new message", List.of(user1.getId(), user2.getId()));
        var updatedLobby = cloneLobby(savedLobby);
        updatedLobby.getInvites().removeAll(List.of(invite1, invite2));

        var subject = new LobbySubject(savedLobby.getId());
        var observer1 = new SseNotificationObserver(new SseEmitter());
        var observer2 = new SseNotificationObserver(new SseEmitter());
        subject.subscribe(observer1);
        subject.subscribe(observer2);

        when(userService.getUserByEmailOrThrow(anyString())).thenReturn(owner);
        when(userService.getUsersListByIds(List.of(user1.getId(), user2.getId()))).thenReturn(List.of(user1, user2));
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.of(savedLobby));
        when(repository.save(updatedLobby)).thenReturn(updatedLobby);
        when(subjectPool.getSubject(anyLong())).thenReturn(subject);
        when(recipientPool.isInPool(anyLong())).thenReturn(true);
        when(recipientPool.getObserver(user1.getId())).thenReturn(observer1);
        when(recipientPool.getObserver(user2.getId())).thenReturn(observer2);
        when(presetService.getPresetFullResponse(any(AppUser.class))).thenReturn(new PresetFullResponse(presetUser1));

        assertEquals(3, savedLobby.getInvites().size());
        assertThat(savedLobby.getInvites().containsAll(List.of(invite1, invite2, invite3))).isTrue();
        assertThat(subject.getObservers().size()).isEqualTo(2);

        var expectedResult = new ActiveLobbyResponse(updatedLobby);
        var result = lobbyActiveService.kickUsers(inviteUsersRequest, auth);

        assertEquals(expectedResult, result);

        assertEquals(1, savedLobby.getInvites().size());
        assertThat(savedLobby.getInvites().contains(invite3)).isTrue();
        assertThat(subject.getObservers().size()).isEqualTo(0);

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyActiveByOwnerId(owner.getId());
        verify(subjectPool, times(1)).getSubject(savedLobby.getId());
        verify(recipientPool, times(4)).isInPool(anyLong());
        verify(userService, times(1)).getUsersListByIds(anyList());
        verify(presetService, times(2)).getPresetFullResponse(any());
        verify(recipientPool, times(4)).getObserver(anyLong());
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
    }

    @Test
    void kickUsers_Throws_Lobby_Not_Found() {
        inviteUsersRequest = new InviteOrKickUsersRequest("new message", List.of(user1.getId(), user2.getId()));

        when(userService.getUserByEmailOrThrow(anyString())).thenReturn(owner);
        when(repository.findLobbyActiveByOwnerId(owner.getId())).thenReturn(Optional.empty());

        assertEquals(3, savedLobby.getInvites().size());
        assertThat(savedLobby.getInvites().containsAll(List.of(invite1, invite2, invite3))).isTrue();

       assertThrows(RestException.class, ()-> lobbyActiveService.kickUsers(inviteUsersRequest, auth));

        assertEquals(3, savedLobby.getInvites().size());
        assertThat(savedLobby.getInvites().containsAll(List.of(invite1, invite2, invite3))).isTrue();

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findLobbyActiveByOwnerId(owner.getId());
        verify(subjectPool, times(0)).getSubject(savedLobby.getId());
        verify(recipientPool, times(0)).isInPool(anyLong());
        verify(userService, times(0)).getUsersListByIds(anyList());
        verify(presetService, times(0)).getPresetFullResponse(any());
        verify(recipientPool, times(0)).getObserver(anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(NotificationData.class));
    }

    @Test
    void updateLobbyTitleAndTime_Success() {

        var newTime = updateTitleTimeRequest.newTime();
        var newTitle = updateTitleTimeRequest.newTitle();

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.ofNullable(savedLobby));
        doCallRealMethod().when(baseUpdateService).setNewValues(updateTitleTimeRequest, savedLobby, owner.getId());
        when(repository.save(savedLobby)).thenReturn(savedLobby);

        var ownerResponse = new AppUserFullResponse(owner);
        List<InvitedUserResponse> invitesUsers = List.of(new InvitedUserResponse(invite1), new InvitedUserResponse(invite2), new InvitedUserResponse(invite3));
        var expected = new ActiveLobbyResponse(savedLobby.getId(), newTime, ownerResponse, savedLobby.getType().toString(), newTitle, invitesUsers);

        var result = lobbyActiveService.updateLobbyTitleAndTime(updateTitleTimeRequest, auth);

        assertEquals(expected, result);
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(1)).setNewValues(updateTitleTimeRequest, savedLobby, owner.getId());
        verify(notificationService, times(1)).notifyLobbyMembers(anyLong(), any(LobbyUpdatedNotificationData.class));
        verify(repository, times(1)).save(savedLobby);
    }

    @Test
    void updateLobbyTitleAndTime_Owner_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(new UsernameNotFoundException(anyString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.updateLobbyTitleAndTime(updateTitleTimeRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(0)).findById(any());
        verify(baseUpdateService, times(0)).setNewValues(any(), any(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(LobbyUpdatedNotificationData.class));
        verify(repository, times(0)).save(any());
    }

    @Test
    void updateLobbyTitleAndTime_Lobby_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.updateLobbyTitleAndTime(updateTitleTimeRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(0)).setNewValues(any(), any(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(LobbyUpdatedNotificationData.class));
        verify(repository, times(0)).save(any());
    }


    @Test
    void updateLobbyTitleAndTime_User_Not_Owner() {

        when(userService.getUserByEmailOrThrow(anyString())).thenReturn(user1);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.ofNullable(savedLobby));
        doCallRealMethod().when(baseUpdateService).isLobbyOwner(savedLobby, user1.getId());
        doCallRealMethod().when(baseUpdateService).setNewValues(updateTitleTimeRequest, savedLobby, user1.getId());

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.updateLobbyTitleAndTime(updateTitleTimeRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(1)).setNewValues(any(), any(), anyLong());
        verify(notificationService, times(0)).notifyLobbyMembers(anyLong(), any(LobbyUpdatedNotificationData.class));
        verify(repository, times(0)).save(any());
    }

    @Test
    void closeLobby_Success() {

        var notificationData = new LobbyClosedNotificationData(savedLobby);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.ofNullable(savedLobby));
        doCallRealMethod().when(baseUpdateService).isLobbyOwner(savedLobby, owner.getId());
        doNothing().when(notificationService).notifyLobbyMembers(savedLobby.getId(), notificationData);
        doNothing().when(subjectPool).removeSubject(savedLobby.getId());

        var result = lobbyActiveService.closeLobby(savedLobby.getId(), auth);

        assertEquals(lobbyResponse, result);

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(1)).isLobbyOwner(savedLobby, owner.getId());
        verify(notificationService, times(1)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(subjectPool, times(1)).removeSubject(savedLobby.getId());
        verify(repository, times(1)).delete(savedLobby);
    }

    @Test
    void closeLobby_Owner_Not_Found() {

        var notificationData = new LobbyClosedNotificationData(savedLobby);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(new UsernameNotFoundException(anyString()));

        assertThrows(UsernameNotFoundException.class, () -> lobbyActiveService.closeLobby(savedLobby.getId(), auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(0)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(0)).isLobbyOwner(savedLobby, owner.getId());
        verify(notificationService, times(0)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(subjectPool, times(0)).removeSubject(savedLobby.getId());
        verify(repository, times(0)).delete(savedLobby);
    }

    @Test
    void closeLobby_Lobby_Not_Found() {
        var notificationData = new LobbyClosedNotificationData(savedLobby);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.closeLobby(savedLobby.getId(), auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(0)).isLobbyOwner(savedLobby, owner.getId());
        verify(notificationService, times(0)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(subjectPool, times(0)).removeSubject(savedLobby.getId());
        verify(repository, times(0)).delete(savedLobby);

    }

    @Test
    void closeLobby_User_Is_Not_Owner() {
        var notificationData = new LobbyClosedNotificationData(savedLobby);

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(user1);
        when(repository.findById(updateTitleTimeRequest.lobbyId())).thenReturn(Optional.ofNullable(savedLobby));
        doCallRealMethod().when(baseUpdateService).isLobbyOwner(savedLobby, user1.getId());

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.closeLobby(savedLobby.getId(), auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(repository, times(1)).findById(updateTitleTimeRequest.lobbyId());
        verify(baseUpdateService, times(1)).isLobbyOwner(savedLobby, user1.getId());
        verify(notificationService, times(0)).notifyLobbyMembers(savedLobby.getId(), notificationData);
        verify(subjectPool, times(0)).removeSubject(savedLobby.getId());
        verify(repository, times(0)).delete(savedLobby);
    }

    @Test
    void getLobbyByIdOrThrow() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> lobbyActiveService.getLobbyByIdOrThrow(anyLong()));

        verify(repository, times(1)).findById(anyLong());
    }

    private Lobby cloneLobby(Lobby lobby) {
        Lobby newLobby = new Lobby(lobby.getId(), lobby.getTitle(), lobby.getTime(), lobby.getOwner());
        newLobby.getInvites().addAll(lobby.getInvites());
        return newLobby;
    }
}