package org.lets_play_be.service.mappers;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.notification.Invite;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyMappers {

    private final AppUserMappers userMapper;

    public LobbyPresetFullResponse toLobbyPresetFullResponse(LobbyPreset lobby) {

        UserShortResponse owner = userMapper.toUserShortResponse(lobby.getOwner());
        List<UserShortResponse> users = lobby.getUsers().stream().map(userMapper::toUserShortResponse).toList();
        String time = FormattingUtils.timeToStringFormatter(lobby.getTime());

        return new LobbyPresetFullResponse(lobby.getId(), lobby.getType().toString(), owner, lobby.getTitle(), time, users);
    }

    public UpdateLobbyTitleAndTimeResponse toUpdateResponse(LobbyPreset lobby) {

        Long id = lobby.getId();
        String type = lobby.getType().toString();
        UserShortResponse owner = userMapper.toUserShortResponse(lobby.getOwner());
        String title = lobby.getTitle();
        String time = FormattingUtils.timeToStringFormatter(lobby.getTime());

        return new UpdateLobbyTitleAndTimeResponse(id, type, owner, title, time);
    }

    public ActiveLobbyResponse toActiveResponse(LobbyActive lobby) {
        Long id = lobby.getId();
        String type = lobby.getType().toString();
        InvitedUserResponse owner = InvitedUserResponse.getInvitedOwner(lobby.getOwner());
        String title = lobby.getTitle();
        String time = FormattingUtils.timeToStringFormatter(lobby.getTime());
        List<InvitedUserResponse> invitedUsers = getListOfInvitedUsersResponses(lobby.getInvites());
        return new ActiveLobbyResponse(id, time, owner, type, title, invitedUsers);
    }

    private List<InvitedUserResponse> getListOfInvitedUsersResponses(List<Invite> invites) {
        return invites.stream().map(InvitedUserResponse::getInvitedUser).toList();
    }
}
