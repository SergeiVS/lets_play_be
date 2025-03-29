package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

import java.io.Serializable;
import java.util.List;

public record ActivateLobbyPresetRequest(@NonNull
                                         Long presetId,
                                         @NotEmpty
                                         String message,
                                         List<Long> userIds) implements Serializable {
}
