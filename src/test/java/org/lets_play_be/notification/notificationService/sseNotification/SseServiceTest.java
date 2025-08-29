package org.lets_play_be.notification.notificationService.sseNotification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SseServiceTest {

    private SseService sseService;


    @BeforeEach
    void setUp() {
        sseService = new SseService();
    }

    @AfterEach
    void tearDown() {
        sseService = null;
    }

    @Test
    void createSseConnection() {
        SseEmitter result = sseService.createSseConnection(1L);

        assertNotNull(result);
        assertInstanceOf(SseEmitter.class, result);
    }
}