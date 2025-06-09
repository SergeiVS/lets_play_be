package org.lets_play_be.dto.inviteDto;

import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.notification.dto.NotificationData;

import java.io.Serializable;

import static org.lets_play_be.dto.userDto.UserShortResponse.toUserShortResponse;

public record InviteResponse(long id,
                             String lobbyTitle,
                             UserShortResponse user,
                             String state,
                             String message,
                             int delayedFor) implements Serializable, NotificationData {


    public InviteResponse(Invite invite) {
        this(invite.getId(),
                invite.getLobby().getTitle(),
                toUserShortResponse(invite.getRecipient()),
                invite.getState().toString(),
                invite.getMessage(),
                invite.getDelayedFor()
        );
    }

}
