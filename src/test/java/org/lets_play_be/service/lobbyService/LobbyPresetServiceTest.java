package org.lets_play_be.service.lobbyService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lets_play_be.dto.lobbyDto.ChangePresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.NewPresetRequest;
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
    AppUser user3;
    Authentication auth;
    LobbyPreset preset;
    LobbyPreset blankPreset;
    NewPresetRequest newPresetRequest;
    OffsetTime time;
    String timeString;

    @BeforeEach
    void setUp() {
        owner = new AppUser(1L, "name", "email", "password", "avatar");
        user1 = new AppUser(2L, "name1", "email1", "password1", "avatar1");
        user2 = new AppUser(3L, "name2", "email2", "password2", "avatar2");
        user3 = new AppUser(4L, "name3", "email3", "password3", "avatar3");

        auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), owner.getPassword());

        time = OffsetTime.now().plusHours(1);
        OffsetTime now = OffsetTime.now();

        preset = new LobbyPreset(1L, "Title", time, owner);
        preset.getUsers().addAll(List.of(user1, user2));

        blankPreset = new LobbyPreset(2L, "", now, owner);

        timeString = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + time.getOffset();
        newPresetRequest = new NewPresetRequest("Title", timeString, List.of(user1.getId(), user2.getId()));
    }

    @AfterEach
    void tearDown() {
        owner = null;
        user1 = null;
        user2 = null;
        auth = null;
        preset = null;
        blankPreset = null;
        newPresetRequest = null;
        time = null;
        timeString = null;
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
    void createNewLobbyPreset_Success() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(userService.getUsersListByIds(newPresetRequest.userIds())).thenReturn(List.of(user1, user2));
        when(presetRepository.save(any(LobbyPreset.class))).thenReturn(preset);

        var expectedResult = new PresetFullResponse(preset);
        var result = presetService.createNewLobbyPreset(newPresetRequest, auth);

        assertEquals(expectedResult, result);

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(userService, times(1)).getUsersListByIds(newPresetRequest.userIds());
        verify(presetRepository, times(1)).save(any(LobbyPreset.class));
    }

    @Test
    void createNewLobbyPreset_Throws_Owner_Not_Found() {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenThrow(UsernameNotFoundException.class);

        assertThrows(UsernameNotFoundException.class, () -> presetService.createNewLobbyPreset(newPresetRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(userService, times(0)).getUsersListByIds(newPresetRequest.userIds());
        verify(presetRepository, times(0)).save(any(LobbyPreset.class));
    }

    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void createNewLobbyPreset_Throws_Users_Not_Found(Class<? extends RuntimeException> e) {

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);
        when(userService.getUsersListByIds(newPresetRequest.userIds())).thenThrow(e);

        assertThrows(e, () -> presetService.createNewLobbyPreset(newPresetRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(userService, times(1)).getUsersListByIds(newPresetRequest.userIds());
        verify(presetRepository, times(0)).save(any(LobbyPreset.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "25:61_85"})
    void createNewLobbyPreset_Throws_Wrong_TimeString_Format(String wrongString) {

        NewPresetRequest falseRequest = new NewPresetRequest("title", wrongString, List.of(user1.getId(), user2.getId()));

        when(userService.getUserByEmailOrThrow(auth.getName())).thenReturn(owner);

        assertThrows(IllegalArgumentException.class, () -> presetService.createNewLobbyPreset(falseRequest, auth));

        verify(userService, times(1)).getUserByEmailOrThrow(auth.getName());
        verify(userService, times(0)).getUsersListByIds(newPresetRequest.userIds());
        verify(presetRepository, times(0)).save(any(LobbyPreset.class));
    }

    @Test
    void getAllUserPresets() {
    }

    @Test
    void addNewUsersToLobbyPreset() {

        ChangePresetUsersRequest request = new ChangePresetUsersRequest(preset.getId(), List.of(user3.getId()));
        LobbyPreset newPreset = new LobbyPreset(preset.getId(), preset.getTitle(), preset.getTime(), preset.getOwner());

        newPreset.getUsers().addAll(preset.getUsers());
        newPreset.getUsers().add(user3);

        when(presetRepository.findById(request.lobbyId())).thenReturn(Optional.of(preset));
        when(userService.getUsersListByIds(List.of(user3.getId()))).thenReturn(List.of(user3));
        when(presetRepository.save(any(LobbyPreset.class))).thenReturn(newPreset);

        var expectedResult = new PresetFullResponse(newPreset);
        var result = presetService.addNewUsersToLobbyPreset(request);

        assertEquals(expectedResult, result);

        verify(presetRepository, times(1)).findById(preset.getId());
        verify(userService, times(1)).getUsersListByIds(List.of(user3.getId()));
        verify(presetRepository, times(1)).save(any(LobbyPreset.class));
    }

    @Test
    void addNewUsersToLobbyPreset_Throws_PresetNotFound() {

        ChangePresetUsersRequest request = new ChangePresetUsersRequest(preset.getId(), List.of(user3.getId()));

        when(presetRepository.findById(request.lobbyId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> presetService.addNewUsersToLobbyPreset(request));

        verify(presetRepository, times(1)).findById(preset.getId());
        verify(userService, times(0)).getUsersListByIds(List.of(user3.getId()));
        verify(presetRepository, times(0)).save(any(LobbyPreset.class));
    }

    @ParameterizedTest
    @ValueSource(classes = {UsernameNotFoundException.class, IllegalArgumentException.class})
    void addNewUsersToLobbyPreset_Throws_UsersNotFound(Class<? extends RuntimeException> e) {

        ChangePresetUsersRequest request = new ChangePresetUsersRequest(preset.getId(), List.of(user3.getId()));

        when(presetRepository.findById(request.lobbyId())).thenReturn(Optional.of(preset));
        when(userService.getUsersListByIds(List.of(user3.getId()))).thenThrow(e);

        assertThrows(e, () -> presetService.addNewUsersToLobbyPreset(request));

        verify(presetRepository, times(1)).findById(preset.getId());
        verify(userService, times(1)).getUsersListByIds(List.of(user3.getId()));
        verify(presetRepository, times(0)).save(any(LobbyPreset.class));
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