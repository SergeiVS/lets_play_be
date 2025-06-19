package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record AppUserFullResponse(
        Long userId,
        String name,
        String email,
        String avatarUrl,
        String[] roles,
        String availability,
        String fromAvailable,
        String toAvailable) implements Serializable {

    public AppUserFullResponse(AppUser appUser) {
        this(
                appUser.getId(),
                appUser.getName(),
                appUser.getEmail(),
                appUser.getAvatarUrl(),
                appUser.getRolesStrings(),
                appUser.getAvailability().getAvailabilityType().toString(),
                timeToStringFormatter(appUser.getAvailability().getFromUnavailable()),
                timeToStringFormatter(appUser.getAvailability().getToUnavailable())
        );
    }
}
