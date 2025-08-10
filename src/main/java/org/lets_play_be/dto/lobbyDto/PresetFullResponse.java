package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.notification.dto.NotificationData;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

@Schema(description = "Sent to client as presentation of lobby current state")
public record PresetFullResponse(
        Long id,
        String type,
        AppUserFullResponse owner,
        String title,
        String time,
        List<InvitedUserResponse> users
) implements Serializable, NotificationData {

    public PresetFullResponse(LobbyPreset lobby) {
        this(
                lobby.getId(),
                lobby.getType().toString(),
                new AppUserFullResponse(lobby.getOwner()),
                lobby.getTitle(),
                timeToStringFormatter(lobby.getTime()),
                lobby.getUsers().stream().map(InvitedUserResponse::new).toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PresetFullResponse response = (PresetFullResponse) o;
        return Objects.equals(id, response.id) && Objects.equals(type, response.type) && Objects.equals(time, response.time) && Objects.equals(title, response.title) && Objects.equals(owner, response.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, owner, title, time);
    }
}
