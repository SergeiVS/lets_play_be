package org.lets_play_be.notification.notificationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.repository.LobbyRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitSubjectsOnStartTest {
    @Mock
    private LobbyRepository repositoryMock;
    @Mock
    private LobbySubjectPool subjectPoolMock;
    @InjectMocks
    private InitSubjectsOnStart initSubjectService;


    @BeforeEach
    void setUp() {
        when(repositoryMock.findAllLobbyIds()).thenReturn(List.of(1L,2L));
        doNothing().when(subjectPoolMock).addSubject(any(LobbySubject.class));
    }

    @AfterEach
    void tearDown() {
        initSubjectService = null;
    }

    @Test
    void init() {

        initSubjectService.init();

        verify(repositoryMock, times(1)).findAllLobbyIds();
        verify(subjectPoolMock, times(2)).addSubject(any(LobbySubject.class));
    }
}