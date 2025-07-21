package org.lets_play_be.notification;

import org.lets_play_be.dto.inviteDto.InviteResponse;
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
        var type = getNotificationType(data);

        return new Notification(uuid, createdAt, type, data);
    }

    private static NotificationType getNotificationType(NotificationData data) {

        return switch (data) {
            case MessageNotificationData ignored -> NotificationType.MESSAGE;
            case LobbyCreatedNotificationData ignored -> NotificationType.LOBBY_CREATED;
            case LobbyUpdatedNotificationData ignored -> NotificationType.LOBBY_UPDATED;
            case LobbyClosedNotificationData ignored -> NotificationType.LOBBY_CLOSED;
            case InviteResponse ignored -> NotificationType.INVITE_UPDATED;
            default -> throw new IllegalStateException("Unexpected value: " + data);
        };
    }
}
