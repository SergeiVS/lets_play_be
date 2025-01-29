package org.lets_play_be.repository;

import org.lets_play_be.entity.AppUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, Long> {
}
