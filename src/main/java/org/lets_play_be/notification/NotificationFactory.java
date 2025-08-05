package org.lets_play_be.notification;

import org.lets_play_be.dto.inviteDto.InviteResponse;
import org.lets_play_be.dto.lobbyDto.PresetFullResponse;
import org.lets_play_be.notification.dto.*;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.lets_play_be.utils.FormattingUtils.dateTimeToStringFormatter;

@Service
public class NotificationFactory {

    public static Notification createNotification(NotificationData data) {
        UUID uuid = UUID.randomUUID();
        String createdAt = dateTimeToStringFormatter(OffsetDateTime.now());
        String type = getNotificationType(data);
        return new Notification(uuid, createdAt, type, data);
    }

    private static String getNotificationType(NotificationData data) {

        return switch (data) {
            case MessageNotificationData ignored -> NotificationType.MESSAGE.name();
            case LobbyCreatedNotificationData ignored -> NotificationType.LOBBY_CREATED.name();
            case LobbyUpdatedNotificationData ignored -> NotificationType.LOBBY_UPDATED.name();
            case LobbyClosedNotificationData ignored -> NotificationType.LOBBY_CLOSED.name();
            case InviteResponse ignored -> NotificationType.INVITE_UPDATED.name();
            case UsersInvitedNotificationData ignored -> NotificationType.LOBBY_NEW_USERS_INVITED.name();
            case UsersKickedNotificationData ignored -> NotificationType.LOBBY_USERS_KICKED.name();
            case PresetFullResponse ignored ->NotificationType.USERS_PRESET_STATE.name();
            default -> throw new IllegalStateException("Unexpected value: " + data);
        };
    }
}
