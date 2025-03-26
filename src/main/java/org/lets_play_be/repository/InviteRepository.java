package org.lets_play_be.repository;

import org.lets_play_be.entity.notification.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {

}
