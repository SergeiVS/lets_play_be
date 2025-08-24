package org.lets_play_be.service.InviteService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.inviteDto.UpdateInviteStateRequest;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.notification.notificationService.sseNotification.SseNotificationService;
import org.lets_play_be.repository.InviteRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.internal.util.collections.CollectionHelper.listOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

    @Mock
    private InviteRepository repositoryMock;
    @Mock
    private AppUserService userServiceMock;
    @Mock
    private SseNotificationService notificationServiceMock;

    @InjectMocks
    private InviteService inviteService;

    AppUser user1;
    AppUser user2;
    AppUser user3;

    Authentication auth2;
    Authentication auth3;

    Lobby lobby1;
    Lobby lobby2;

    Invite invite1;
    Invite invite2;
    Invite invite3;

    @BeforeEach
    void setUp() {
        user1 = new AppUser(1L, "Name1", "email1@email.com", "password1", "url1");
        user2 = new AppUser(2L, "Name2", "email2@email.com", "password2", "url2");
        user3 = new AppUser(3L, "Name3", "email3@email.com", "password3", "url3");

        auth2 = new UsernamePasswordAuthenticationToken(user2.getEmail(), user2.getPassword());
        auth3 = new UsernamePasswordAuthenticationToken(user3.getEmail(), user3.getPassword());

        lobby1 = new Lobby(1L, "title1", OffsetTime.now().plusHours(1), user1);
        lobby2 = new Lobby(2L, "title2", OffsetTime.now().plusHours(2), user2);

        invite1 = new Invite(1L, user3, lobby1, "message1");
        invite2 = new Invite(2L, user3, lobby2, "message2");
        invite3 = new Invite(3L, user2, lobby1, "message3");


        lobby1.getInvites().add(invite1);
        lobby2.getInvites().add(invite2);
    }

    @AfterEach
    void tearDown() {
        user1 = null;
        user2 = null;
        user3 = null;

        auth2 = null;
        auth3 = null;

        lobby1 = null;
        lobby2 = null;

        invite1 = null;
        invite2 = null;

    }

    @Test
    void updateIsDelivered() {
        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));

        when(repositoryMock.save(any())).thenReturn(invite1);

        inviteService.updateIsDelivered(invite1.getId());

        assertThat(invite1.isDelivered()).isTrue();
        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(1)).save(invite1);
    }

    @Test
    void updateIsSeen() {

        when(userServiceMock.getUserByEmailOrThrow(user3.getEmail())).thenReturn(user3);
        when(repositoryMock.findById(invite2.getId())).thenReturn(Optional.ofNullable(invite2));
        when(repositoryMock.save(any())).thenReturn(invite2);

        assertThat(invite1.isDelivered()).isFalse();
        assertThat(invite2.isSeen()).isFalse();

        inviteService.updateIsSeen(auth3, invite2.getId());

        assertThat(invite2.isSeen()).isTrue();
        assertThat(invite2.isDelivered()).isTrue();

        verify(repositoryMock, times(1)).findById(invite2.getId());
        verify(repositoryMock, times(1)).save(invite2);
        verify(userServiceMock, times(1)).getUserByEmailOrThrow(user3.getEmail());
    }

    @Test
    void updateIsSeen_throws_IllegalArgumentException_UserIsNotRecipient() {

        when(userServiceMock.getUserByEmailOrThrow(user2.getEmail())).thenReturn(user2);
        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> inviteService.updateIsSeen(auth2, invite1.getId()),
                "User is not recipient of this invite");

        verify(userServiceMock, times(1)).getUserByEmailOrThrow(user2.getEmail());
        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(0)).save(invite2);

    }

    @Test
    void getAllUserInviteResponses() {
        invite2.setDelivered(true);

        when(repositoryMock.findInvitesByUserId(user3.getId())).thenReturn(listOf(invite1, invite2));
        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));
        when(repositoryMock.save(invite1)).thenReturn(invite1);

        var response1 = new InviteResponse(invite1);
        var response2 = new InviteResponse(invite2);
        var response3 = new InviteResponse(invite3);

        assertThat(invite1.isDelivered()).isFalse();
        assertThat(invite2.isDelivered()).isTrue();

        List<InviteResponse> result1 = inviteService.getAllUserInviteResponses(user3.getId());

        assertThat(result1.size()).isEqualTo(2);

        assertThat(result1.contains(response1)).isTrue();
        assertThat(result1.contains(response2)).isTrue();
        assertThat(result1.contains(response3)).isFalse();

        assertThat(invite1.isDelivered()).isTrue();
        assertThat(invite2.isDelivered()).isTrue();

        verify(repositoryMock, times(1)).findInvitesByUserId(user3.getId());
        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(1)).save(invite1);
    }

    @Test
    void getAllInvitesByUser_Throws_Exception_InvitesNotFoundInviteResponses() {
        when(repositoryMock.findInvitesByUserId(4L)).thenReturn(listOf());

        assertThrowsExactly(RestException.class,
                () -> inviteService.getAllUserInviteResponses(4L),
                "No invites were found");

        verify(repositoryMock, times(1)).findInvitesByUserId(4L);
        verify(repositoryMock, times(0)).findById(any(Long.class));
        verify(repositoryMock, times(0)).save(any(Invite.class));
    }

    @Test
    void getAllInvitesByLobbyId() {
        when(repositoryMock.findInvitesByLobbyId(1L)).thenReturn(listOf(invite1, invite3));

        var response1 = new InviteResponse(invite1);
        var response2 = new InviteResponse(invite3);

        List<InviteResponse> result = inviteService.getAllInvitesByLobbyId(lobby1.getId());

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(response1)).isTrue();
        assertThat(result.contains(response2)).isTrue();

        verify(repositoryMock, times(1)).findInvitesByLobbyId(1L);
    }

    @Test
    void getAllInvitesByLobbyId_Throws_Exception_InvitesNotFound() {
        when(repositoryMock.findInvitesByLobbyId(4L)).thenReturn(listOf());

        assertThrowsExactly(RestException.class,
                () -> inviteService.getAllInvitesByLobbyId(4L),
                "No invites were found");

        verify(repositoryMock, times(1)).findInvitesByLobbyId(4L);
    }

    @Test
    void updateInviteState_Not_Delayed() {
        var request = new UpdateInviteStateRequest(1L, "accepted", 1);

        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));
        when(repositoryMock.save(invite1)).thenReturn(invite1);

        assertThat(invite1.getState().equals(InviteState.PENDING)).isTrue();
        assertThat(invite1.getDelayedFor() == 0).isTrue();

        var result = inviteService.updateInviteState(request, 3);

        var expectedResponse = new InviteResponse(invite1);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(invite1.getState().equals(InviteState.ACCEPTED)).isTrue();
        assertThat(invite1.getDelayedFor() == 0).isTrue();

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(1)).save(invite1);
        verify(notificationServiceMock, times(1))
                .notifyLobbyMembers(anyLong(), any(InviteResponse.class));
    }

    @Test
    void updateInviteState_Delayed() {
        var request = new UpdateInviteStateRequest(1L, "Delayed", 1);

        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));
        when(repositoryMock.save(invite1)).thenReturn(invite1);

        assertThat(invite1.getState().equals(InviteState.PENDING)).isTrue();
        assertThat(invite1.getDelayedFor() == 0).isTrue();

        var result = inviteService.updateInviteState(request, 3);

        var expectedResponse = new InviteResponse(invite1);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(invite1.getState().equals(InviteState.DELAYED)).isTrue();
        assertThat(invite1.getDelayedFor() == 1).isTrue();

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(1)).save(invite1);
        verify(notificationServiceMock, times(1))
                .notifyLobbyMembers(anyLong(), any(InviteResponse.class));
    }

    @Test
    void updateInviteState_Throws_State_Not_Found() {
        var request = new UpdateInviteStateRequest(1L, "SomeState", 0);

        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> inviteService.updateInviteState(request, 3),
                "New invite state do not meet an Enum");

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(0)).save(invite1);
        verify(notificationServiceMock, times(0))
                .notifyLobbyMembers(anyLong(), any(InviteResponse.class));
    }

    @Test
    void updateInviteState_Throws_DelayedFor_Zero() {
        var request = new UpdateInviteStateRequest(1L, "Delayed", 0);

        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> inviteService.updateInviteState(request, 3),
                "If newState equals delayed, value of delayedFor should be positive number over 0");

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(0)).save(invite1);
        verify(notificationServiceMock, times(0))
                .notifyLobbyMembers(anyLong(), any(InviteResponse.class));
    }

    @Test
    void updateInviteState_Throws_False_Recipient() {
        var request = new UpdateInviteStateRequest(1L, "Delayed", 1);

        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> inviteService.updateInviteState(request, 6),
                "User is not recipient of this invite");

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(repositoryMock, times(0)).save(invite1);
        verify(notificationServiceMock, times(0))
                .notifyLobbyMembers(anyLong(), any(InviteResponse.class));
    }

    @Test
    void removeInvite() {
        when(repositoryMock.findById(invite2.getId())).thenReturn(Optional.ofNullable(invite2));
        when(userServiceMock.getUserByEmailOrThrow(anyString())).thenReturn(user2);

        var result = inviteService.removeInvite(invite2.getId(), auth2);
        var expectedResponse = new InviteResponse(invite2);

        assertEquals(expectedResponse, result);

        verify(repositoryMock, times(1)).findById(invite2.getId());
        verify(userServiceMock, times(1)).getUserByEmailOrThrow(anyString());
        verify(repositoryMock, times(1)).delete(invite2);
        verify(notificationServiceMock, times(1)).unsubscribeUserFromSubject(anyLong(), anyLong());
    }

    @Test
    void removeInvite_Trow_NotLobbyOwner() {
        when(repositoryMock.findById(invite1.getId())).thenReturn(Optional.ofNullable(invite1));
        when(userServiceMock.getUserByEmailOrThrow(user2.getEmail())).thenReturn(user2);

        assertThrowsExactly(RestException.class,
                () -> inviteService.removeInvite(invite1.getId(), auth2),
                "User is not recipient of this invite");

        verify(repositoryMock, times(1)).findById(invite1.getId());
        verify(userServiceMock, times(1)).getUserByEmailOrThrow(anyString());
        verify(repositoryMock, times(0)).delete(invite2);
        verify(notificationServiceMock, times(0)).unsubscribeUserFromSubject(anyLong(), anyLong());
    }

    @Test
    void setInvitesDelivered_RecipientIdsListNotEmpty() {
        assertFalse(invite1.isDelivered());
        assertFalse(invite2.isDelivered());
        assertFalse(invite3.isDelivered());

        inviteService.setInvitesDelivered(List.of(invite1, invite3), List.of(user3.getId(), user2.getId()));

        assertTrue(invite1.isDelivered());
        assertFalse(invite2.isDelivered());
        assertTrue(invite3.isDelivered());
    }

    @Test
    void setInvitesDelivered_RecipientIdsListEmpty() {
        assertFalse(invite1.isDelivered());
        assertFalse(invite2.isDelivered());
        assertFalse(invite3.isDelivered());

        inviteService.setInvitesDelivered(List.of(invite1, invite3), List.of());

        assertFalse(invite1.isDelivered());
        assertFalse(invite2.isDelivered());
        assertFalse(invite3.isDelivered());
    }
}