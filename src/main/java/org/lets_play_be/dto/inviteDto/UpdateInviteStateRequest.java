package org.lets_play_be.dto.inviteDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateInviteStateRequest(@NotNull long inviteId,
                                       @NotNull long userId,
                                       @NotEmpty String newState,
                                       //By newState == delayed delayedFor must be >0
                                       @NotNull
                                       @PositiveOrZero int delayedFor) {
}
