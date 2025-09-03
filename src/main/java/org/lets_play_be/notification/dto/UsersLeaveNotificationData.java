package org.lets_play_be.notification.dto;

import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.Lobby;

import java.io.Serializable;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record UsersLeaveNotificationData(long lobbyId,
                                         String time,
                                         String ownerName,
                                         String lobbyType,
                                         String title,
                                         List<InvitedUserResponse> invitedUsers
) implements Serializable, NotificationData {

    public UsersLeaveNotificationData(Lobby lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                lobby.getOwner().getName(),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(InvitedUserResponse::new).toList()
        );
    }
}
