package org.lets_play_be.notification.dto;

import org.lets_play_be.notification.NotificationType;

import java.io.Serializable;
import java.util.UUID;

public record Notification(UUID id,
                           String createdAt,
                           NotificationType type,
                           NotificationData data
) implements Serializable {
}