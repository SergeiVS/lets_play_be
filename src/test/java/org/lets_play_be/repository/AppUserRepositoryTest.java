package org.lets_play_be.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.user.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Profile("test")
class AppUserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AppUserRepository repository;

    AppUser user1;
    AppUser user2;

    @BeforeEach
    void setUp() {
        user1 = new AppUser("User1", "email1@email.com", "password1", "");
        entityManager.persist(user1);

        user2 = new AppUser("User2", "email2@email.com", "password2", "");
        entityManager.persist(user2);

        entityManager.flush();
    }

    @AfterEach
    void tearDown() {
        entityManager.remove(user1);
        entityManager.remove(user2);
        entityManager.flush();
    }

    @Test
    public void existsByEmail() {

        boolean result1 = repository.existsByEmail("email1@email.com");
        boolean result2 = repository.existsByEmail("email3@email.com");

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
    }

    @Test
    void existsByName() {

        boolean result1 = repository.existsByName("User1");
        boolean result2 = repository.existsByName("User2");
        boolean result3 = repository.existsByName("User3");

        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(true);
        assertThat(result3).isEqualTo(false);
    }

    @Test
    void findAppUserByEmail() {

        Optional<AppUser> foundUser = repository.findAppUserByEmail("email1@email.com");
        Optional<AppUser> notFoundUser = repository.findAppUserByEmail("email3@email.com");

        assertThat(foundUser.isPresent()).isTrue();
        assertThat(notFoundUser.isPresent()).isFalse();
    }

    @Test
    void getAppUsersById() {

        long id1 = (long) entityManager.getId(user1);
        long id2 = (long) entityManager.getId(user2);

        List<AppUser> foundUsers = repository.getAppUsersById(List.of(id1, id2, 55L));

        assertThat(foundUsers.size()).isEqualTo(2);
        assertThat(foundUsers.getFirst()).isEqualTo(user1);
        assertThat(foundUsers.getLast()).isEqualTo(user2);
    }
}