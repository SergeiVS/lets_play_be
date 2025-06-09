package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;

import java.io.Serializable;

public record UserShortResponse(
        Long id,
        String name
) implements Serializable {

    public UserShortResponse(AppUser user) {
        this(user.getId(), user.getName());
    }
}
