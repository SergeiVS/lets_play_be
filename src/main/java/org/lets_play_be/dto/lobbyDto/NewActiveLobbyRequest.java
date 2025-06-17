package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Received from client to create new Active lobby")
public record NewActiveLobbyRequest(@NotEmpty
                                    String title,
                                    @NotEmpty
                                    @Pattern(regexp = "[0-2]{1}\\d{1}:[0-5]{1}\\d{1}:[0-5]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                            message = "Wrong time format. Expected: HH:MM:SS+/-HH:MM")
                                    String time,

                                    String message,
                                    @NotNull
                                    @NotEmpty
                                    List<Long> userIds) implements Serializable {
}
