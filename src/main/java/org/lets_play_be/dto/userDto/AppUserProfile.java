package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;

import java.io.Serializable;
import java.util.List;

public record AppUserProfile(
        Long id,
        String name,
        String email,
        List<AppUserRole> roles
) implements Serializable {

    public AppUserProfile(AppUser user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRoles());
    }
}
