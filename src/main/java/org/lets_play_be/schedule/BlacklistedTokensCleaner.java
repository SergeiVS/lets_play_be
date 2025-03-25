package org.lets_play_be.schedule;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.service.blacklistedTokenService.BlacklistedTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlacklistedTokensCleaner {

    private final int SCHEDULE_DELAY_MINUTES = 60  * 24;

    private final BlacklistedTokenService blacklistedTokenService;

    @Scheduled(fixedDelay = SCHEDULE_DELAY_MINUTES * 60 * 1000)
    public void checkAndCleanBlacklistedTokens() {
        blacklistedTokenService.checkAndCleanBlacklistedTokens();
    }
}
