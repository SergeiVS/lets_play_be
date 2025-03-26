package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.util.List;

public record NewActiveLobbyRequest(@NotEmpty
                                    String title,
                                    @Pattern(regexp = "[0-2]{1}\\d{1}:[0-6]{1}\\d{1}:[0-6]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                            message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                    String time,

                                    String message,
                                    @NotNull
                                    List<Long> userIds) implements Serializable {
}
