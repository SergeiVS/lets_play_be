package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.Lobby;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

@Schema(description = "Will sent to client as current Lobby state presentation")
public record LobbyResponse(
        long id,
        String time,
        AppUserFullResponse owner,
        String lobbyType,
        String title,
        List<InvitedUserResponse> users
) implements Serializable {

    public LobbyResponse(Lobby lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                new AppUserFullResponse(lobby.getOwner()),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(InvitedUserResponse::new).toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        LobbyResponse that = (LobbyResponse) o;

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
