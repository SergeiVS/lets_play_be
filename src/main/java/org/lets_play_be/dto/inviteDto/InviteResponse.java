package org.lets_play_be.dto.inviteDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.notification.dto.NotificationData;

import java.io.Serializable;

@Schema(description = "Were returned to client. Presents invite`s current state")
public record InviteResponse(long id,
                             String lobbyTitle,
                             UserShortResponse user,
                             String state,
                             String message,
                             int delayedFor) implements Serializable, NotificationData {


    public InviteResponse(Invite invite) {
        this(
                invite.getId(),
                invite.getLobby().getTitle(),
                new UserShortResponse(invite.getRecipient()),
                invite.getState().toString(),
                invite.getMessage(),
                invite.getDelayedFor()
        );
    }

}
