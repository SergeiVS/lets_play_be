package org.lets_play_be.notification.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.notification.notificationDto.MessageNotificationData;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.appUserService.GetUserProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.lets_play_be.notification.NotificationFactory.createNotification;

@Service
@RequiredArgsConstructor
public class SubscribeSseService {

    private final SseEmitters emitters;
    private final AppUserService appUserService;;

    public SseEmitter openSseStream(Authentication auth) throws IOException {

        var id = appUserService.getUserIdByEmailOrThrow(auth.getName());

        emitters.create(id);

        List<Long> ids = Collections.singletonList(id);

        emitters.sendEvent(createNotification(new MessageNotificationData("Connection complete")), ids);

        return emitters.getEmitters().get(id);
    }
}
