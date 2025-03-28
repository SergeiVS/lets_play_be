package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LobbyActiveCRUDServiceTest {

    @Mock
    LobbyActiveRepoService repoService;

    @Mock
    AppUserService userService;
    

    @BeforeEach
    void setUp() {

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