package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.lobby.LobbyBase;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;

import static org.lets_play_be.utils.ValidationUtils.validateLobbyTypeString;

@Service
@RequiredArgsConstructor
public class LobbyBaseUpdateService {


    protected void setNewValues(UpdateLobbyTitleAndTimeRequest request, LobbyBase presetForChange, OffsetTime newTime) {
        if (!request.newTitle().equals(presetForChange.getTitle())) {
            presetForChange.setTitle(request.newTitle());
        }
        if (presetForChange.getTime() != newTime) {
            presetForChange.setTime(newTime);
        }
    }

}
