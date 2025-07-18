package org.lets_play_be.repository;

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
        token1 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3NTI2ODk2MTIsImV4cCI6MTc4NDIyNTYxMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkVtYWlsIjoianJvY2tldEBleGFtcGxlLmNvbSIsIlJvbGUiOiJVU0VSIn0.SEaiOYj9iwx2-7uTO9GV0r5tMNnE--vgidJeuia7row";
        token2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE3NTI2ODk2MTIsImV4cCI6MTc4NDIyNTYxMiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFtcGxlLmNvbSIsIkVtYWlsIjoianJvY2tldDJAZXhhbXBsZS5jb20iLCJSb2xlIjoiVVNFUiJ9.QFaRIbrD0yNNzT2uSWjXQmsQN5kZwnJ99Ona_jZVrRc";

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

        var result1 = repository.getByRefreshToken(token1);
        var result2 = repository.getByRefreshToken(token2);
        var resultFalse = repository.getByRefreshToken("FalseToken");

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