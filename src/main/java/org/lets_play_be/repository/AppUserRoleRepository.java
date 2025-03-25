package org.lets_play_be.repository;

import org.lets_play_be.entity.user.AppUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, Long> {

    Optional<AppUserRole> findByNameIgnoreCase(String name);
}
