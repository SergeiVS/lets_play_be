package org.lets_play_be.repository;

import org.lets_play_be.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {


    Optional<BlacklistedToken> getByRefreshToken(String tokenHash);


    @Modifying
    int removeBlacklistedTokensByExpiresAtBefore(OffsetDateTime now);

}
