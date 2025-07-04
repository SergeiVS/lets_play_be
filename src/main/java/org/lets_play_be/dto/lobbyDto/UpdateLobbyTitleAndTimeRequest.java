package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "received from client used to set new values for title and time fields")
public record UpdateLobbyTitleAndTimeRequest(@NotNull
                                             Long lobbyId,
                                             @NotEmpty
                                             String newTitle,
                                             @NotEmpty
                                             @Pattern(regexp ="([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d[+-](0\\d|1[0-4]):[0-5]\\d",
                                                     message = "Wrong time format. Expected: HH:MM:SS+/-HH:MM")
                                             String newTime) {
}
