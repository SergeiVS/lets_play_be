package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LobbyActiveCRUDService {

    private final LobbyActiveRepoService repoService;
//    TODO add method logic
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {
        return null;
    }
}
