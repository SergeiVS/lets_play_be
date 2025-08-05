package org.lets_play_be.notification.dto;

import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.LobbyActive;

import java.io.Serializable;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record UsersKickedNotificationData(long lobbyId,
                                          String time,
                                          InvitedUserResponse owner,
                                          String lobbyType,
                                          String title,
                                          List<InvitedUserResponse> invitedNewUsers
) implements Serializable, NotificationData {

    public UsersKickedNotificationData(LobbyActive lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                new InvitedUserResponse(lobby.getOwner()),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(InvitedUserResponse::new).toList()
        );
    }
}
