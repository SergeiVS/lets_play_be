package org.lets_play_be.service.lobbyService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class LobbyActiveServiceTest {

    @Mock
    LobbyActiveRepository repoService;

    @Mock
    AppUserService userService;

    @Mock
    InviteService inviteService;

    LobbyMappers lobbyMappers;

    @InjectMocks
    LobbyActiveService lobbyActiveService;

    NewActiveLobbyRequest newLobbyRequest;

    ActiveLobbyResponse lobbyResponse;

    LobbyActive presaveLobby;

    LobbyActive savedLobby;

    AppUser owner;

    AppUser user;

    UserAvailability ownerAvailability;

    UserAvailability userAvailability;

    @BeforeEach
    void setUp() {


        newLobbyRequest = new NewActiveLobbyRequest("title", "18:00:00+00:00", "test message", List.of(1L));

    }

    @Test
    void createActiveLobby() {
    }

    @Test
    void updateLobbyTitleAndTime() {
    }

    @Test
    void getLobbyByIdOrThrow() {
    }
}