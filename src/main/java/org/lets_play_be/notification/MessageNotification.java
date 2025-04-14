package org.lets_play_be.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class MessageNotification extends NotificationData implements Serializable  {
    private final String message;
}
