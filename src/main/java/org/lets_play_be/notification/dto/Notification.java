package org.lets_play_be.notification.dto;

import java.io.Serializable;
import java.util.UUID;

public record Notification(UUID id,
                           String createdAt,
                           String type,
                           NotificationData data) implements Serializable {
}