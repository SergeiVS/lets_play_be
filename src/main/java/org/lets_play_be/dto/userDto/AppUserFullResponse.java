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
        @Nullable
        String fromAvailable,
        @Nullable
        String toAvailable) implements Serializable {
}
