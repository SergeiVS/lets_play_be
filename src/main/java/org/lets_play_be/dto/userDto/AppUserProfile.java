package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.AppUserRole;

import java.io.Serializable;
import java.util.List;

public record AppUserProfile(
        Long id,
        String name,
        String email,
        List<AppUserRole> roles
) implements Serializable {

    public static AppUserProfile from(AppUser user) {
        return new AppUserProfile(user.getId(), user.getName(), user.getEmail(), user.getRoles());
    }
}
