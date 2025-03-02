package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotNull;

public record UserDataUpdateRequest(@NotNull(message = "UserId could not be null")
                                    Long userId,
                                    String newName,
                                    String newAvatarUrl) {
}
