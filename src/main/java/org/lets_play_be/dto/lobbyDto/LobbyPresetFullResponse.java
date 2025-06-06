package org.lets_play_be.dto.lobbyDto;

import org.lets_play_be.dto.userDto.UserShortResponse;

import java.io.Serializable;
import java.util.List;

public record LobbyPresetFullResponse(
        Long id,
        String type,
        UserShortResponse owner,
        String title,
        String time,
        List<UserShortResponse> users
) implements Serializable {
}
