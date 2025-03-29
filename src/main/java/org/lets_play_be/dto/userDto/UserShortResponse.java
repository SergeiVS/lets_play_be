package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;

import java.io.Serializable;

public record UserShortResponse(
        Long id,
        String name
) implements Serializable {

    public static UserShortResponse toUserShortResponse(AppUser user) {
        return new UserShortResponse(user.getId(), user.getName());
    }
}
