package org.lets_play_be.subscription;

import org.lets_play_be.dto.notification.Notification;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

public class Subscription {
    AppUser recipient;
    FluxSink<ServerSentEvent<Notification>> sink;
}
