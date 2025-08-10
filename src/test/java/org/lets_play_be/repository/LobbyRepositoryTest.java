package org.lets_play_be.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;

import java.time.OffsetTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Profile("test")
class LobbyRepositoryTest {

    @Autowired
    private LobbyActiveRepository repository;
    @Autowired
    private EntityManager em;

    AppUser user1;
    AppUser user2;
    AppUser user3;

    Lobby lobby1;
    Lobby lobby2;

    @BeforeEach
    void setUp() {
        user1 = new AppUser("User1", "email1@email.com", "Password1", "N/A");
        em.persist(user1);
        user2 = new AppUser("User2", "email2@email.com", "Password2", "N/A");
        em.persist(user2);

        user3 = new AppUser("User3", "email3@email.com", "Password3", "N/A");
        em.persist(user3);

        lobby1 = new Lobby("Title1", OffsetTime.now().plusHours(2), user1);
        em.persist(lobby1);

        lobby2 = new Lobby("Title2", OffsetTime.now().plusHours(3), user2);
        em.persist(lobby2);

        em.flush();
    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void existsLobbyActiveByOwner() {

        assertTrue(repository.existsLobbyActiveByOwner(user1));
        assertTrue(repository.existsLobbyActiveByOwner(user2));
        assertFalse(repository.existsLobbyActiveByOwner(user3));
    }

    @Test
    void findAllLobbyIds() {

        List<Long> ids = repository.findAllLobbyIds();

        assertFalse(ids.isEmpty());
        assertThat(ids.size()).isEqualTo(2);
        assertTrue(ids.contains(lobby1.getId()));
        assertTrue(ids.contains(lobby2.getId()));
    }

    @Test
    void findLobbyActiveByOwnerId() {
        var result1 = repository.findLobbyActiveByOwnerId(user1.getId());
        assertTrue(result1.isPresent());
        assertThat(result1.get()).isEqualTo(lobby1);

        var result2 = repository.findLobbyActiveByOwnerId(user2.getId());
        assertTrue(result2.isPresent());
        assertThat(result2.get()).isEqualTo(lobby2);

        var result3 = repository.findLobbyActiveByOwnerId(user3.getId());
        assertTrue(result3.isEmpty());
    }
}