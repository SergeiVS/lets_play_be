package org.lets_play_be.notification.notificationDto;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@RequiredArgsConstructor
public class Notification implements Serializable {

    private final UUID id;
    private final String createdAt;
    private final String type;
    private final NotificationData data;
}