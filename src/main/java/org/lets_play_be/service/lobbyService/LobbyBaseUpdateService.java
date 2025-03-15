package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyBaseUpdateService {
    private final LobbyPresetCRUDService presetService;
    private final LobbyActiveCRUDService activeService;

//    TODO Add Method logic
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {

        return null;
    }

}
