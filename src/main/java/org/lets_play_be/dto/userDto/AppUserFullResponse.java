package org.lets_play_be.dto.userDto;

import org.springframework.lang.Nullable;

import java.io.Serializable;

public record AppUserFullResponse(
        Long userId,
        String name,
        String email,
        String avatarUrl,
        String[] roles,
        String availability,
        String fromAvailable,
        String toAvailable) implements Serializable {
}
