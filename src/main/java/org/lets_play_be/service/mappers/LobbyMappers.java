package org.lets_play_be.service.mappers;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.LobbyPreset;
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
        String time = FormattingUtils.TIME_TO_STRING_FORMATTER(lobby.getTime());

        return new LobbyPresetFullResponse(lobby.getId(), lobby.getType().toString(), owner, lobby.getTitle(), time, users);
    }

    public UpdateLobbyTitleAndTimeResponse toUpdateResponse(LobbyPreset lobby) {

        Long id = lobby.getId();
        String type = lobby.getType().toString();
        UserShortResponse owner = userMapper.toUserShortResponse(lobby.getOwner());
        String title = lobby.getTitle();
        String time = FormattingUtils.TIME_TO_STRING_FORMATTER(lobby.getTime());

        return new UpdateLobbyTitleAndTimeResponse(id, type, owner, title, time);
    }
}
