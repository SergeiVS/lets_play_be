package org.lets_play_be.dto.userDto;

import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.user.AppUser;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvitedUserResponse that)) return false;
        return userId == that.userId && delayedFor == that.delayedFor && Objects.equals(name, that.name) && Objects.equals(inviteState, that.inviteState) && Objects.equals(availability, that.availability) && Objects.equals(unavailableTo, that.unavailableTo) && Objects.equals(unavailableFrom, that.unavailableFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, availability, unavailableFrom, unavailableTo, inviteState, delayedFor);
    }
}
