package org.lets_play_be.dto.inviteDto;

import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.notification.notificationDto.NotificationData;

import java.io.Serializable;

import static org.lets_play_be.dto.userDto.UserShortResponse.toUserShortResponse;

public record InviteResponse(long id,
                             String lobbyTitle,
                             UserShortResponse user,
                             String state,
                             String message,
                             int delayedFor) implements Serializable, NotificationData {


    public InviteResponse toInviteResponse(Invite invite) {

        long id = invite.getId();
        String lobbyTitle = invite.getLobby().getTitle();
        UserShortResponse user = toUserShortResponse(invite.getRecipient());
        String state = invite.getState().toString();
        String message = invite.getMessage();
        int delayedFor = invite.getDelayedFor();

        return new InviteResponse(id, lobbyTitle, user, state, message, delayedFor);
    }

}
