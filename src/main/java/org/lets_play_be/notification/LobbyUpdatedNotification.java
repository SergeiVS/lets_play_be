package org.lets_play_be.notification;

import java.io.Serializable;

public record LobbyUpdatedNotification() implements Serializable, NotificationData {
}
