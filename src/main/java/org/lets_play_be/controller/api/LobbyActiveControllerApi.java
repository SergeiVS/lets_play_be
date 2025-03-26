package org.lets_play_be.controller.api;

import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/lobby/active")
public interface LobbyActiveControllerApi {
    @PostMapping
    public ResponseEntity<ActiveLobbyResponse> addNewLobbyActive(@RequestBody
                                                                 @Validated
                                                                 NewActiveLobbyRequest request,
                                                                 Authentication authentication);
}
