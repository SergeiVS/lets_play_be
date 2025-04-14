package org.lets_play_be.dto.inviteDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.notification.NotificationData;

import java.io.Serializable;

import static org.lets_play_be.dto.userDto.UserShortResponse.toUserShortResponse;

@RequiredArgsConstructor
@Getter
public class InviteResponse extends NotificationData implements Serializable {

    private final long id;
    private final String lobbyTitle;
    private final UserShortResponse user;
    private final String state;
    private final String message;
    private final int delayedFor;

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
