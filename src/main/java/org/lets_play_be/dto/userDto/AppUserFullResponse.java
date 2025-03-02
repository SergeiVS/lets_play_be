package org.lets_play_be.dto.userDto;

import org.springframework.lang.Nullable;

import java.io.Serializable;

public record AppUserFullResponse(
        Long userId,
        String name,
        String email,
        String avatarUrl,
        String availability,
        String[] roles,
        String fromAvailable,
        String toAvailable) implements Serializable {
}
