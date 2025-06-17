package org.lets_play_be.service.blacklistedTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.repository.BlacklistedTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class BlacklistedTokenService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(BlacklistedTokenService.class);

    @Async
    public void checkAndCleanBlacklistedTokens() {
        LOGGER.info("SCHEDULED | DB CLEAN UP | Cleaning up blacklisted tokens");

        int amountDeleted = blacklistedTokenRepository.removeBlacklistedTokensByExpiresAtBefore(OffsetDateTime.now());

        LOGGER.info("SCHEDULED | DB CLEAN UP | Deleted {} blacklisted tokens", amountDeleted);
    }
}
