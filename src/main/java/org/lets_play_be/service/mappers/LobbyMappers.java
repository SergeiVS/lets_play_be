package org.lets_play_be.service.mappers;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
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

        AppUserFullResponse owner = userMapper.toFullUserResponse(lobby.getOwner());
        List<UserShortResponse> users = lobby.getUsers().stream().map(userMapper::toUserShortResponse).toList();
        String time = FormattingUtils.TIME_TO_STRING_FORMATTER(lobby.getTime());

        return new LobbyPresetFullResponse(lobby.getId(), lobby.getType().toString(), owner, lobby.getTitle(), time, users);
    }
}
