package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.user.AppUser;

import java.io.Serializable;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record InvitedUserResponse(
        long userId,
        String name,
        String availability,
        String unavailableFrom,
        String unavailableTo,
        String inviteState,
        int delayedFor
) implements Serializable {

    public InvitedUserResponse(Invite invite) {
        this(
                invite.getRecipient().getId(),
                invite.getRecipient().getName(),
                invite.getRecipient().getAvailability().getAvailabilityType().toString(),
                timeToStringFormatter(invite.getRecipient().getAvailability().getFromUnavailable()),
                timeToStringFormatter(invite.getRecipient().getAvailability().getToUnavailable()),
                invite.getState().toString(),
                invite.getDelayedFor()
        );
    }

    public InvitedUserResponse(AppUser user) {
        this(
                user.getId(),
                user.getName(),
                AvailabilityEnum.AVAILABLE.toString(),
                "00.00.00+01.00",
                "00.00.00+01.00",
                InviteState.ACCEPTED.toString(),
                0
        );
    }
}
