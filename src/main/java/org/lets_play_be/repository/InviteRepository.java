package org.lets_play_be.repository;

import org.lets_play_be.entity.notification.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
}
