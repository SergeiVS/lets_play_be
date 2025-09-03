package org.lets_play_be.dto.inviteDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.lets_play_be.entity.enums.InviteState;

@Schema(description = "Received from client with new InviteState. If newState equals- delayed, delayed for must be over zero. " +
        "In case of other values of newState field. delayedFor will be ignored")
public record UpdateInviteStateRequest(@NotNull long inviteId,
                                       @NotEmpty InviteState newState,
                                       @NotNull
                                       @PositiveOrZero int delayedFor) {
}
