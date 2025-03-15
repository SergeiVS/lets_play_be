package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.lets_play_be.entity.enums.LobbyType;

public record UpdateLobbyTitleAndTimeRequest(@NotNull
                                             Long id,
                                             @NotEmpty
                                             String type,
                                             @NotEmpty
                                             String newTitle,
                                             @NotEmpty
                                             @Pattern(regexp = "[0-2]{1}\\d{1}:[0-6]{1}\\d{1}:[0-6]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                                     message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                             String newTime) {
}
