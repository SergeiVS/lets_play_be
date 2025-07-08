package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record UserDataUpdateRequest(
        @NotNull
        String newName,
        @NotNull
        String newAvatarUrl) implements Serializable {
}
