package org.lets_play_be.dto.notification;

import org.lets_play_be.dto.userDto.UserShortResponse;

import java.io.Serializable;


public record Notification(UserShortResponse recipient,
                           String createdAt,
                           String type,
                           NotificationData data) implements Serializable {

}