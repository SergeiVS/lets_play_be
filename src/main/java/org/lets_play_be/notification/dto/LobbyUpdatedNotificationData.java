package org.lets_play_be.notification.dto;

import org.lets_play_be.entity.lobby.Lobby;

import java.io.Serializable;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record LobbyUpdatedNotificationData(long lobbyId,
                                           String ownerName,
                                           String title,
                                           String time) implements Serializable, NotificationData {

    public LobbyUpdatedNotificationData(Lobby lobby) {
        this(
                lobby.getId(),
                lobby.getOwner().getName(),
                lobby.getTitle(),
                timeToStringFormatter(lobby.getTime())
        );
    }
}
