package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.notification.dto.NotificationData;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

@Schema(description = "Will sent to client as current Lobby state presentation")
public record LobbyShortResponse(
        long id,
        String time,
        AppUserFullResponse owner,
        String lobbyType,
        String title,
        List<UserShortResponse> users
) implements Serializable, NotificationData {

    public LobbyShortResponse(Lobby lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                new AppUserFullResponse(lobby.getOwner()),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(UserShortResponse::new).toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LobbyShortResponse that = (LobbyShortResponse) o;
        return id == that.id
                && Objects.equals(time, that.time)
                && Objects.equals(title, that.title)
                && Objects.equals(lobbyType, that.lobbyType)
                && Objects.equals(owner, that.owner)
                && Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, owner, lobbyType, title, users);
    }
}
