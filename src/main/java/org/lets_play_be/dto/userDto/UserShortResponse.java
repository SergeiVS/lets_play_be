package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.user.AppUser;

import java.io.Serializable;
import java.util.Objects;

public record UserShortResponse(
        long id,
        String name
) implements Serializable {

    public UserShortResponse(AppUser user) {
        this(user.getId(), user.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserShortResponse that = (UserShortResponse) o;
        return id == that.id && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
