package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record ChangeLobbyPresetUsersRequest(@NotNull(message = "Id could not be null")
                                            Long lobbyId,
                                            @NotEmpty(message = "Id Array could not be empty")
                                            List<Long> usersId) implements Serializable {
}
