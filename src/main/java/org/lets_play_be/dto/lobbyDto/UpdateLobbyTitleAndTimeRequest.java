package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.lets_play_be.entity.enums.LobbyType;

@Schema(description = "received from client used to set new values for title and time fields")
public record UpdateLobbyTitleAndTimeRequest(@NotNull
                                             Long id,
                                             @NotEmpty
                                             String newTitle,
                                             @NotEmpty
                                             @Pattern(regexp = "[0-2]{1}\\d{1}:[0-5]{1}\\d{1}:[0-5]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                                     message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                             String newTime) {
}
