package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.exception.RestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.lets_play_be.utils.ValidationUtils.validateLobbyTypeString;

@Service
@RequiredArgsConstructor
public class LobbyBaseUpdateService {

    private final LobbyPresetCRUDService presetService;
    private final LobbyActiveCRUDService activeService;

    //    TODO Add Method logic
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {

        validateLobbyTypeString(request.type());

        LobbyType type = LobbyType.valueOf(request.type().toUpperCase());

        switch (type) {
            case PRESET:
                return presetService.updateLobbyTitleAndTime(request);
            case ACTIVE:
                return activeService.updateLobbyTitleAndTime(request);
            default:
                throw new RestException("Failed Type", HttpStatus.BAD_REQUEST);
        }

    }

}
