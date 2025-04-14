package org.lets_play_be.controller.api;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/v1/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public interface SseNotificationControllerApi {

    @GetMapping()
   SseEmitter openSseStream(Authentication authentication) throws IOException;


}
