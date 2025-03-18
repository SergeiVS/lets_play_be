package org.lets_play_be.repository;

import org.lets_play_be.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);
    boolean existsByName(String name);

    Optional<AppUser> findAppUserByEmail(String email);

    @Query("SELECT u FROM AppUser u WHERE u.id IN :ids")
    List<AppUser> getAppUsersById(List<Long> ids);

}
