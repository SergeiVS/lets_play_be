package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.utils.FormattingUtils;

import java.io.Serializable;

public record InvitedUserResponse(
        long userId,
        String name,
        String availability,
        String unavailableFrom,
        String unavailableTo,
        String inviteState,
        int delayedFor
) implements Serializable {

    public static InvitedUserResponse getInvitedUser(Invite invite) {
        AppUser user = invite.getRecipient();
        long userId = user.getId();
        String name = user.getName();
        String availability = user.getAvailability().getAvailabilityType().toString();
        String unavailableFor = FormattingUtils.timeToStringFormatter(user.getAvailability().getFromUnavailable());
        String unavailableTo = FormattingUtils.timeToStringFormatter(user.getAvailability().getToUnavailable());
        String inviteState = invite.getState().toString();
        int delayedFor = invite.getDelayedFor();

        return new InvitedUserResponse(userId, name, availability, unavailableFor, unavailableTo, inviteState, delayedFor);
    }

    public static InvitedUserResponse getInvitedOwner(AppUser user) {
        long userId = user.getId();
        String name = user.getName();
        String availability = AvailabilityEnum.AVAILABLE.toString();
        String time = "00.00.00+01.00";
        String inviteState = InviteState.ACCEPTED.toString();
        int delayedFor = 0;
        return new InvitedUserResponse(userId,name,availability,time,time,inviteState,delayedFor);
    }
}
