package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.Invite;
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
        AppUser user = invite.getUser();
        long userId = user.getId();
        String name = user.getName();
        String availability = user.getAvailability().getAvailabilityType().toString();
        String unavailableFor = FormattingUtils.TIME_TO_STRING_FORMATTER(user.getAvailability().getFromUnavailable());
        String unavailableTo = FormattingUtils.TIME_TO_STRING_FORMATTER(user.getAvailability().getToUnavailable());
        String inviteState = invite.getState().toString();
        int delayedFor = invite.getDelayedFor();

        return new InvitedUserResponse(userId, name, availability, unavailableFor, unavailableTo, inviteState, delayedFor);
    }
}
