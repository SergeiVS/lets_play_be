package org.lets_play_be.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Profile;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Profile("test")
class LobbyPresetRepositoryTest {

    @Autowired
    private LobbyPresetRepository repository;
    @Autowired
    private TestEntityManager em;

    private AppUser user1;
    private AppUser user2;

    private LobbyPreset preset1;
    private LobbyPreset preset2;


    @BeforeEach
    void setUp() {
        user1 = new AppUser("User1", "email1@email.com", "Password1", "N/A");
        em.persist(user1);
        user2 = new AppUser("User2", "email2@email.com", "Password2", "N/A");
        em.persist(user2);

        preset1 = new LobbyPreset("Title1", OffsetTime.now().plusHours(2), user1, new ArrayList<>());
        em.persist(preset1);
        preset2 = new LobbyPreset("Title2", OffsetTime.now().plusHours(2), user2, new ArrayList<>());
        em.persist(preset2);

        em.flush();
    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void findByOwnerId() {
        List<LobbyPreset> result1 = repository.findByOwnerId(user1.getId());
        List<LobbyPreset> result2 = repository.findByOwnerId(user2.getId());

        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.getFirst()).isEqualTo(preset1);
        assertThat(result1.contains(preset2)).isFalse();

        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.getFirst()).isEqualTo(preset2);
        assertThat(result2.contains(preset1)).isFalse();
    }
}