package org.lets_play_be.notification.notificationService.sseNotification;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.notification.NotificationObserver;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.*;

class SseLiveRecipientPoolTest {

    private SseLiveRecipientPool recipientPool;
    private SseEmitter emitter1;
    private SseEmitter emitter2;

    @BeforeEach
    void setUp() {
        recipientPool = new SseLiveRecipientPool();
        emitter1 = new SseEmitter();
        emitter2 = new SseEmitter();
    }

    @AfterEach
    void tearDown() {
        recipientPool = null;
        emitter1 = null;
        emitter2 = null;
    }

    @Test
    void addObserver() {

        assertNotNull(recipientPool);
        assertFalse(recipientPool.isInPool(1L));
        assertFalse(recipientPool.isInPool(2L));

        recipientPool.addObserver(1L, emitter1);

        boolean result1 = recipientPool.isInPool(1L);
        boolean result2 = recipientPool.isInPool(2L);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void removeRecipient() {

        assertNotNull(recipientPool);

        recipientPool.addObserver(1L, emitter1);
        recipientPool.addObserver(2L, emitter2);

        assertTrue(recipientPool.isInPool(1L));
        assertTrue(recipientPool.isInPool(2L));

        recipientPool.removeRecipient(2L);

        boolean result1 = recipientPool.isInPool(1L);
        boolean result2 = recipientPool.isInPool(2L);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void getObserver() {
        assertNotNull(recipientPool);

        recipientPool.addObserver(1L, emitter1);

        NotificationObserver result = recipientPool.getObserver(1L);

        assertNotNull(result);
        assertInstanceOf(SseNotificationObserver.class, result);
    }
}