package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

@Schema(description = "received from client. Contains a newly added users")
public record ChangePresetUsersRequest(@NotNull(message = "Id could not be null")
                                       Long lobbyId,
                                       @NotEmpty(message = "Id Array could not be empty")
                                       List<Long> usersIds) implements Serializable {
}
