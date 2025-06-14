package org.lets_play_be.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.BlacklistedToken;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Profile("test")
class BlacklistedTokenRepositoryTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private BlacklistedTokenRepository repository;


    BlacklistedToken blacklistedToken1;
    BlacklistedToken blacklistedToken2;
    String token1;
    String token2;
    AppUser user1;
    AppUser user2;

    OffsetDateTime expiresAt;

    @BeforeEach
    void setUp() {

        user1 = new AppUser("User1", "email1@email.com", "password1", "url");
        em.persist(user1);

        user2 = new AppUser("User2", "email2@email.com", "password2", "url");
        em.persist(user2);

        expiresAt = OffsetDateTime.now().plusDays(1);
        token1 = "jwtService.generateRefreshToken(user.getEmail(),user.getRoles())1";
        token2 = "jwtService.generateRefreshToken(user.getEmail(),user.getRoles())2";

        blacklistedToken1 = new BlacklistedToken(user1, token1, expiresAt);
        em.persist(blacklistedToken1);

        blacklistedToken2 = new BlacklistedToken(user2, token2, expiresAt);
        em.persist(blacklistedToken2);

        em.flush();
    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void getByRefreshToken() {

        Optional<Entity> result1 = repository.getByRefreshToken(token1);
        Optional<Entity> result2 = repository.getByRefreshToken(token2);
        Optional<Entity> resultFalse = repository.getByRefreshToken("FalseToken");

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertFalse(resultFalse.isPresent());
    }

    @Test
    void removeBlacklistedTokensByExpiresAtBefore() {
        OffsetDateTime expiresAfter = OffsetDateTime.now().plusDays(2);

        int result = repository.removeBlacklistedTokensByExpiresAtBefore(expiresAfter);

        assertEquals(2, result);

    }
}