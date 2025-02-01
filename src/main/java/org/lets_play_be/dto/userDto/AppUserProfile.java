package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.AppUserRole;

import java.io.Serializable;
import java.util.List;

public record AppUserProfile(
        long id,
        String name,
        String email,
        String avatarUrl,
        List<AppUserRole> roles
) implements Serializable {

    public static AppUserProfile from(AppUser user) {
        return new AppUserProfile(user.getId(), user.getName(), user.getEmail(), user.getAvatarUrl(), user.getRoles());
    }
}
