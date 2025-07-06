package org.lets_play_be.notification.notificationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.notification.NotificationSubject;

import java.time.OffsetTime;

import static org.assertj.core.api.Assertions.assertThat;

class LobbySubjectPoolTest {

    private LobbySubjectPool lobbySubjectPool;

    private LobbyActive testLobby1;
    private LobbyActive testLobby2;
    private LobbySubject testSubject1;
    private LobbySubject testSubject2;

    @BeforeEach
    void setUp() {
        lobbySubjectPool = new LobbySubjectPool();
        testLobby1 = new LobbyActive(1L, "null", OffsetTime.now(), new AppUser());
        testLobby2 = new LobbyActive(2L, "null", OffsetTime.now(), new AppUser());

        testSubject1 = new LobbySubject(testLobby1.getId());
        testSubject2 = new LobbySubject(testLobby2.getId());

    }

    @AfterEach
    void tearDown() {
        lobbySubjectPool = null;
        testLobby1 = null;
        testLobby2 = null;
        testSubject1 = null;
        testSubject2 = null;
    }

    @Test
    void addSubject_GetSubject() {

        NotificationSubject nullResult1 = lobbySubjectPool.getSubject(testLobby1.getId());
        NotificationSubject nullResult2 = lobbySubjectPool.getSubject(testLobby2.getId());

        assertThat(nullResult1).isNull();
        assertThat(nullResult2).isNull();

        lobbySubjectPool.addSubject(testSubject1);
        lobbySubjectPool.addSubject(testSubject2);

        NotificationSubject result1 = lobbySubjectPool.getSubject(testSubject1.getLobbyId());
        NotificationSubject result2 = lobbySubjectPool.getSubject(testSubject2.getLobbyId());

        assertThat(result1).isEqualTo(testSubject1);
        assertThat(result2).isEqualTo(testSubject2);
    }

    @Test
    void removeSubject() {
        lobbySubjectPool.addSubject(testSubject1);
        lobbySubjectPool.addSubject(testSubject2);

        NotificationSubject checkResult1 = lobbySubjectPool.getSubject(testSubject1.getLobbyId());
        NotificationSubject checkResult2 = lobbySubjectPool.getSubject(testSubject2.getLobbyId());

        assertThat(checkResult1).isEqualTo(testSubject1);
        assertThat(checkResult2).isEqualTo(testSubject2);

        lobbySubjectPool.removeSubject(testLobby1.getId());

        NotificationSubject subjectNotFoundResult = lobbySubjectPool.getSubject(testLobby1.getId());
        NotificationSubject subjectFoundResult = lobbySubjectPool.getSubject(testLobby2.getId());

        assertThat(subjectNotFoundResult).isNull();
        assertThat(subjectFoundResult).isEqualTo(testSubject2);
    }
}