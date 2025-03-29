package org.lets_play_be.repository;


import org.lets_play_be.entity.user.UserAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, Long> {
}
