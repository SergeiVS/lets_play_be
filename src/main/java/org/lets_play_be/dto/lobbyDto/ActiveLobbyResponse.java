package org.lets_play_be.dto.lobbyDto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.entity.lobby.LobbyActive;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;

@Schema(description = "Will sent to client as current Lobby state presentation")
public record ActiveLobbyResponse(long id,
                                  String date,
                                  InvitedUserResponse owner,
                                  String lobbyType,
                                  String title,
                                  List<InvitedUserResponse> invitedUsers) implements Serializable {

    public ActiveLobbyResponse(LobbyActive lobby) {
        this(
                lobby.getId(),
                timeToStringFormatter(lobby.getTime()),
                new InvitedUserResponse(lobby.getOwner()),
                lobby.getType().toString(),
                lobby.getTitle(),
                lobby.getInvites().stream().map(InvitedUserResponse::new).toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ActiveLobbyResponse response)) return false;
        return id == response.id && Objects.equals(date, response.date) && Objects.equals(title, response.title) && Objects.equals(lobbyType, response.lobbyType) && Objects.equals(owner, response.owner) && Objects.equals(invitedUsers, response.invitedUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, owner, lobbyType, title, invitedUsers);
    }
}
