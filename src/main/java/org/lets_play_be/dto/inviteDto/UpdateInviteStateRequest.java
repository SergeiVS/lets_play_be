package org.lets_play_be.dto.inviteDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Received from client with new InviteState. If newState equals- delayed, delayed for must be over zero. " +
        "In case of other values of newState field. delayedFor will be ignored")
public record UpdateInviteStateRequest(@NotNull long inviteId,
                                       @NotEmpty String newState,
                                       //By newState == delayed delayedFor must be >0
                                       @NotNull
                                       @PositiveOrZero int delayedFor) {
}
