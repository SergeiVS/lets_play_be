package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.lobbyDto.PresetFullResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyPresetServiceTest {

    @Mock
    LobbyPresetRepository presetRepository;
    @Mock
    LobbyBaseUpdateService baseUpdateService;
    @Mock
    AppUserService userService;

    @InjectMocks
    LobbyPresetService presetService;

    AppUser owner;
    AppUser user1;
    AppUser user2;
    Authentication auth;
    LobbyPreset preset;
    LobbyPreset blankPreset;

    @BeforeEach
    void setUp() {
        owner = new AppUser(1L, "name", "email", "password", "avatar");
        user1 = new AppUser(2L, "name1", "email1", "password1", "avatar1");
        user2 = new AppUser(3L, "name2", "email2", "password2", "avatar2");

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), owner.getPassword());

        OffsetTime time = OffsetTime.now().plusHours(1);
        OffsetTime now = OffsetTime.now();

        preset = new LobbyPreset(1L, "Title", time, owner);
        preset.getUsers().addAll(List.of(user1, user2));

        blankPreset = new LobbyPreset(2L, "", now, owner);
    }

    @AfterEach
    void tearDown() {
        owner = null;
        user1 = null;
        user2 = null;
        auth = null;
        preset = null;
        blankPreset = null;
    }

    @Test
    void getUsersLobbyPreset_PresetFound_Success() {
        when(presetRepository.findUniqueByOwnerId(owner.getId())).thenReturn(Optional.ofNullable(preset));
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);

        var expectedResponse = new PresetFullResponse(preset);
        var userShot1 = new UserShortResponse(user1);
        var userShot2 = new UserShortResponse(user2);

        var result = presetService.getUsersLobbyPreset(owner.getId(), auth);

        assertEquals(expectedResponse, result);

        assertThat(result.users()).hasSize(2);
        assertThat(result.users()).contains(userShot1, userShot2);

        verify(presetRepository, times(1)).findUniqueByOwnerId(owner.getId());
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(presetRepository);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(baseUpdateService);
    }

    @Test
    void getUsersLobbyPreset_PresetNotFound_Success() {
        when(presetRepository.findUniqueByOwnerId(owner.getId())).thenReturn(Optional.empty());
        when(presetRepository.save(any(LobbyPreset.class))).thenReturn(blankPreset);
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);

        var expectedResponse = new PresetFullResponse(blankPreset);

        var result = presetService.getUsersLobbyPreset(owner.getId(), auth);

        assertEquals(expectedResponse, result);

        assertThat(result.users()).hasSize(0);

        verify(presetRepository, times(1)).findUniqueByOwnerId(owner.getId());
        verify(presetRepository, times(1)).save(any(LobbyPreset.class));
        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verifyNoMoreInteractions(presetRepository);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(baseUpdateService);
    }

    @Test
    void getUsersLobbyPreset_Throws_IdDontCorrespondAuth() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(user1);

        assertThrows(IllegalArgumentException.class, () -> presetService.getUsersLobbyPreset(owner.getId(), auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());

        verifyNoMoreInteractions(userService);
        verifyNoInteractions(presetRepository);
        verifyNoInteractions(baseUpdateService);
    }

    @Test
    void getUsersLobbyPreset_Throws_OwnerNotFound() {
        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> presetService.getUsersLobbyPreset(owner.getId(), auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());

        verifyNoMoreInteractions(userService);
        verifyNoInteractions(presetRepository);
        verifyNoInteractions(baseUpdateService);
    }


    @Test
    void createNewLobbyPreset() {
    }

    @Test
    void getAllUserPresets() {
    }

    @Test
    void addNewUsersToLobbyPreset() {
    }

    @Test
    void removeUserFromPreset() {
    }

    @Test
    void removeLobbyPreset() {
    }

    @Test
    void updateLobbyTitleAndTime() {
    }

    @Test
    void getPresetByIdOrThrow() {
    }
}