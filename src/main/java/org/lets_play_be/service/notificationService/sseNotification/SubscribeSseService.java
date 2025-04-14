package org.lets_play_be.service.notificationService.sseNotification;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.service.appUserService.GetUserProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class SubscribeSseService {

    private final SseEmitters emitters;
    private final GetUserProfileService userProfileService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public SseEmitter openSseStream(Authentication auth) throws IOException {

        AppUserProfile recipient = userProfileService.getUserProfile(auth.getName());

        var id = recipient.id();
        emitters.add(id);

        SseEmitter emitter = emitters.getEmitters().get(id);

        executor.execute(() -> {
            try {
                emitter.send("Connection complete");
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }
}
