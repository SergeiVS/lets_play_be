package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record ChangeUsersListRequest(@NotEmpty
                                     String message,
                                     @NotNull
                                     List<Long> usersIds) implements Serializable {
}
