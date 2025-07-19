package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.utils.FormattingUtils;

import java.time.OffsetTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class LobbyBaseUpdateServiceTest {

    private final LobbyBaseUpdateService service = new LobbyBaseUpdateService();

    private AppUser owner;
    private LobbyActive lobbyForChange;
    private UpdateLobbyTitleAndTimeRequest newLobbyRequest;
    private String newTime;
    private String newTitle;

    @BeforeEach
    void setUp() {

        newTime = "18:00:00+00:00";
        newTitle = "New Title";

        owner = new AppUser(1L, "name", "email@email.com", "password", "url", new ArrayList<>(), new UserAvailability());
        lobbyForChange = new LobbyActive(1L, "Title", OffsetTime.now().plusHours(1), owner);
        newLobbyRequest = new UpdateLobbyTitleAndTimeRequest(lobbyForChange.getId(), newTitle, newTime);
    }

    @AfterEach
    void tearDown() {
        owner = null;
        lobbyForChange = null;
    }

    @Test
    void setNewValues_Success() {

        service.setNewValues(newLobbyRequest,lobbyForChange, owner.getId());

        assertEquals(newTitle, lobbyForChange.getTitle());
        assertEquals(FormattingUtils.timeStringToOffsetTime(newTime), lobbyForChange.getTime());
    }

    @Test
    void setNewValues_NotOwner_Throws() {

        assertThrowsExactly(IllegalArgumentException.class,
                () -> service.setNewValues(newLobbyRequest,lobbyForChange, 2L),
                "User with Id: " + 2L + " is not owner of this lobby.");
    }

    @Test
    void isLobbyOwner() {

        assertThrowsExactly(IllegalArgumentException.class,
                () -> service.isLobbyOwner(lobbyForChange, 2L),
                "User with Id: " + 2L + " is not owner of this lobby.");

        assertDoesNotThrow(() -> service.isLobbyOwner(lobbyForChange, 1L));
    }
}