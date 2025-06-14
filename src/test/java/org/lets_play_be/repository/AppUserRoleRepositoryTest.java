package org.lets_play_be.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Profile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Profile("test")
class AppUserRoleRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppUserRoleRepository repository;

    AppUserRole admin;
    AppUserRole user;


    @BeforeEach
    void setUp() {

        admin = new AppUserRole(UserRoleEnum.ROLE_ADMIN.name());
        user = new AppUserRole(UserRoleEnum.ROLE_USER.name());
        em.persist(admin);
        em.persist(user);
        em.flush();
    }

    @AfterEach
    void tearDown() {
        em.clear();
    }

    @Test
    void findByNameIgnoreCase() {

        Optional<AppUserRole> resultPositive = repository.findByNameIgnoreCase("role_admin");
        Optional<AppUserRole> resultNegative = repository.findByNameIgnoreCase("role_superadmin");

        assertThat(resultPositive).isPresent();
        assertThat(resultNegative).isNotPresent();
    }
}