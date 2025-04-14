package org.lets_play_be.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.lets_play_be.utils.FormattingUtils.dateTimeToStringFormatter;

public class NotificationFactory {

    public static Notification createNotification(NotificationData data) {
        UUID uuid = UUID.randomUUID();
        String createdAt = dateTimeToStringFormatter(OffsetDateTime.now());
        String type = getNotificationType(data);
        return new Notification(uuid, createdAt, type, data);
    }

    private static String getNotificationType(NotificationData data) {

        return switch (data) {
            case MessageNotification m -> NotificationType.MESSAGE.name();
            case LobbyCreatedNotificationData lc -> NotificationType.LOBBY_CREATED.name();
            default -> throw new IllegalStateException("Unexpected value: " + data);
        };
    }
}
