package org.lets_play_be.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

@DataJpaTest
@Profile("test")
class InviteRepositoryTest {

    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private EntityManager em;

    private AppUser user1;
    private AppUser user2;

    private LobbyActive lobby1;
    private LobbyActive lobby2;

    private Invite invite1;
    private Invite invite2;



    @BeforeEach
    void setUp() {
        user1 = new AppUser("User1", "email1@email.com","password1", "N/A");
        em.persist(user1);
        user2 = new AppUser("User2", "email2@email.com","password2", "N/A");
        em.persist(user2);

        lobby1 = new LobbyActive("Title1", OffsetTime.now().plusHours(2), user1);
        em.persist(lobby1);
        lobby2 = new LobbyActive("Title2", OffsetTime.now().plusHours(3), user2);
        em.persist(lobby2);

        invite1=new Invite(user2,lobby1,"MESSAGE1");
        em.persist(invite1);
        invite2=new Invite(user1,lobby2,"MESSAGE2");
        em.persist(invite2);

        lobby1.getInvites().add(invite1);
        em.persist(lobby1);
        lobby2.getInvites().add(invite2);
        em.persist(lobby2);

        em.flush();

    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void findInvitesByLobbyId() {

        List<Invite> result1 = inviteRepository.findInvitesByLobbyId(lobby1.getId());
        List<Invite> result2 = inviteRepository.findInvitesByLobbyId(lobby2.getId());

        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.getFirst()).isEqualTo(invite1);
        Assertions.assertFalse(result1.contains(invite2));

        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.getFirst()).isEqualTo(invite2);
        Assertions.assertFalse(result2.contains(invite1));

    }

    @Test
    void findInvitesByUserId() {
        List<Invite> result1 = inviteRepository.findInvitesByUserId(user2.getId());
        List<Invite> result2 = inviteRepository.findInvitesByUserId(user1.getId());

        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.getFirst()).isEqualTo(invite1);
        Assertions.assertFalse(result1.contains(invite2));

        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.getFirst()).isEqualTo(invite2);
        Assertions.assertFalse(result2.contains(invite1));
    }

    @Test
    void findNotDeliveredInvitesByUserId() {
        List<Invite> result1 = inviteRepository.findNotDeliveredInvitesByUserId(user2.getId());
        List<Invite> result2 = inviteRepository.findNotDeliveredInvitesByUserId(user1.getId());

        assertThat(result1.size()).isEqualTo(1);
        assertThat(result1.getFirst()).isEqualTo(invite1);
        Assertions.assertFalse(result1.contains(invite2));

        assertThat(result2.size()).isEqualTo(1);
        assertThat(result2.getFirst()).isEqualTo(invite2);
        Assertions.assertFalse(result2.contains(invite1));

    }
}