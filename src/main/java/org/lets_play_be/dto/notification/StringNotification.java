package org.lets_play_be.dto.notification;

import java.io.Serializable;

public record StringNotification(String message) implements Serializable, NotificationData {
}
