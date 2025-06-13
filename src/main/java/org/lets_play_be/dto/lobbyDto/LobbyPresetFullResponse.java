package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.lobby.LobbyPreset;

import java.io.Serializable;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

@Schema(description = "Sent to client as presentation of lobby current state")
public record LobbyPresetFullResponse(
        Long id,
        String type,
        UserShortResponse owner,
        String title,
        String time,
        List<UserShortResponse> users
) implements Serializable {

    public LobbyPresetFullResponse(LobbyPreset lobby) {
        this(
                lobby.getId(),
                lobby.getType().toString(),
                new UserShortResponse(lobby.getOwner()),
                lobby.getTitle(),
                timeToStringFormatter(lobby.getTime()),
                lobby.getUsers().stream().map(UserShortResponse::new).toList()
        );
    }
}
