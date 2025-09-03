package org.lets_play_be.notification.dto;

import org.lets_play_be.dto.lobbyDto.LobbyResponse;
import org.lets_play_be.entity.lobby.Lobby;

import java.io.Serializable;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

public record LobbyUpdatedNotificationData(LobbyResponse lobby) implements Serializable, NotificationData {
    public LobbyUpdatedNotificationData(Lobby lobby) {
        this(new LobbyResponse(lobby));
    }
}
