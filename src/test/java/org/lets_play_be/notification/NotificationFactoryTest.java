package org.lets_play_be.notification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.dto.*;

import java.time.OffsetTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.lets_play_be.notification.NotificationFactory.createNotification;

class NotificationFactoryTest {

    private AppUser testUser;
    private LobbyActive testLobby;
    private String testMessage;
    private Invite testInvite;

    @BeforeEach
    void setUp() {
        testUser = new AppUser(1L, "Name", "email@email.com", "password", "url");
        testLobby = new LobbyActive(1L, "title", OffsetTime.now().plusHours(1), testUser);
        testMessage = "test message";
        testInvite = new Invite(1L, testUser, testLobby, testMessage);
    }

    @AfterEach
    void tearDown() {
        testUser = null;
        testLobby = null;
        testMessage = null;
        testInvite = null;
    }

    @Test
    void createNotification_Message() {

        NotificationData data = new MessageNotificationData(testMessage);

        Notification result = createNotification(data);

        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.data()).isEqualTo(data);
        assertThat(result.type()).isEqualTo(NotificationType.MESSAGE.toString());
    }

    @Test
    void createNotification_LobbyCreated() {

        NotificationData data = new LobbyCreatedNotificationData(testLobby);

        Notification result = createNotification(data);

        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.data()).isEqualTo(data);
        assertThat(result.type()).isEqualTo(NotificationType.LOBBY_CREATED.toString());
    }

    @Test
    void createNotification_LobbyUpdated() {

        NotificationData data = new LobbyUpdatedNotificationData(testLobby);

        Notification result = createNotification(data);

        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.data()).isEqualTo(data);
        assertThat(result.type()).isEqualTo(NotificationType.LOBBY_UPDATED.toString());
    }

    @Test
    void createNotification_LobbyDeleted() {

        NotificationData data = new LobbyClosedNotificationData(testLobby);

        Notification result = createNotification(data);

        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.data()).isEqualTo(data);
        assertThat(result.type()).isEqualTo(NotificationType.LOBBY_CLOSED.toString());
    }

    @Test
    void createNotification_InviteUpdated() {

        NotificationData data = new InviteResponse(testInvite);

        Notification result = createNotification(data);

        assertThat(result.id()).isNotNull();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.data()).isEqualTo(data);
        assertThat(result.type()).isEqualTo(NotificationType.INVITE_UPDATED.toString());
    }

    @Test
    void createNotification_Throws() {

        NotificationData data = new FalseNotificationData(testMessage);

        assertThrowsExactly(IllegalStateException.class, () -> createNotification(data), "Unexpected value: " + data);
    }

    private record FalseNotificationData(String message) implements NotificationData {
    }
}