package org.lets_play_be.notification.dto;

import java.io.Serializable;


public record MessageNotificationData(String message) implements Serializable, NotificationData {
}
