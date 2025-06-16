package org.lets_play_be.service;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.repository.UserAvailabilityRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAvailabilityService {

    private final UserAvailabilityRepository repository;

    public UserAvailability saveAvailability(UserAvailability availability) {
        return repository.save(availability);
    }

}
