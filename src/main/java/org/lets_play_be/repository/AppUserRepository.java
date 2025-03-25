package org.lets_play_be.repository;

import org.lets_play_be.entity.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
    boolean existsByName(String name);

    Optional<AppUser> findAppUserByEmail(String email);

}
