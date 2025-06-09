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

    private final LobbyPresetCRUDService presetService;
    private final LobbyActiveService activeService;

    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {

        validateLobbyTypeString(request.type());

        LobbyType type = LobbyType.valueOf(request.type().toUpperCase());

        return switch (type) {
            case PRESET -> presetService.updateLobbyTitleAndTime(request, auth);
            case ACTIVE -> activeService.updateLobbyTitleAndTime(request, auth);
        };

    }

    protected static void setNewValues(UpdateLobbyTitleAndTimeRequest request, LobbyBase presetForChange, OffsetTime newTime) {
        if (!request.newTitle().equals(presetForChange.getTitle())) {
            presetForChange.setTitle(request.newTitle());
        }
        if (presetForChange.getTime() != newTime) {
            presetForChange.setTime(newTime);
        }
    }

}
