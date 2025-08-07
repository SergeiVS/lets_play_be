package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;

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
        String unavailableFrom,
        String unavailableTo) implements Serializable {

    public AppUserFullResponse(AppUser appUser) {
        this(
                appUser.getId(),
                appUser.getName(),
                appUser.getEmail(),
                appUser.getAvatarUrl(),
                appUser.getRolesStrings(),
                appUser.getAvailability().getAvailabilityType().toString(),
                timeToStringFormatter(appUser.getAvailability().getUnavailableFrom()),
                timeToStringFormatter(appUser.getAvailability().getUnavailableTo())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserFullResponse response = (AppUserFullResponse) o;
        return Objects.equals(userId, response.userId)
                && Objects.equals(name, response.name)
                && Objects.equals(email, response.email)
                && Objects.deepEquals(roles, response.roles)
                && Objects.equals(avatarUrl, response.avatarUrl)
                && Objects.equals(unavailableTo, response.unavailableTo)
                && Objects.equals(availability, response.availability)
                && Objects.equals(unavailableFrom, response.unavailableFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, avatarUrl, Arrays.hashCode(roles), availability, unavailableFrom, unavailableTo);

    }
}
