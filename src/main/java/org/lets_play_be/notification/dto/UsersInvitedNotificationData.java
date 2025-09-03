package org.lets_play_be.notification.dto;

import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.Lobby;

import java.io.Serializable;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record UsersInvitedNotificationData(long lobbyId,
                                           String time,
                                           String ownerName,
                                           String lobbyType,
                                           String title,
                                           List<InvitedUserResponse> invitedUsers
) implements Serializable, NotificationData {

    public UsersInvitedNotificationData(Lobby lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                lobby.getOwner().getEmail(),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(InvitedUserResponse::new).toList()
        );
    }
}
