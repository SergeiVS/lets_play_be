package org.lets_play_be.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;


public record MessageNotification(String message)  implements Serializable, NotificationData  {

}
