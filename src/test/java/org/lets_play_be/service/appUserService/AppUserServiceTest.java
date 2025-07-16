package org.lets_play_be.service.appUserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.repository.AppUserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.internal.util.collections.CollectionHelper.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    AppUserRepository repository;

    @InjectMocks
    AppUserService appUserService;

    AppUser user1;
    AppUser user2;

    AppUserRole role;

    UserAvailability userAvailability1;
    UserAvailability userAvailability2;


    @BeforeEach
    void setUp() {
        role = new AppUserRole(UserRoleEnum.ROLE_USER.name());

        userAvailability1 = new UserAvailability(1L, AvailabilityEnum.AVAILABLE);
        userAvailability2 = new UserAvailability(2L, AvailabilityEnum.AVAILABLE);

        user1 = new AppUser(1L, "User1", "email@email.com", "password", "");
        user2 = new AppUser(2L, "User2", "email2@email.com", "password2", "");

        user1.setAvailability(userAvailability1);
        user1.getRoles().add(role);

        user2.setAvailability(userAvailability2);
        user2.getRoles().add(role);
    }

    @AfterEach
    void tearDown() {
        role = null;
        userAvailability1 = null;
        userAvailability2 = null;
        user1 = null;
        user2 = null;
    }

    @Test
    void getAppUserFullData() {

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        AppUserFullResponse expectedResponse = new AppUserFullResponse(user1);
        AppUserFullResponse actualResponse = appUserService.getAppUserFullData(user1.getEmail());

        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.availability(), actualResponse.availability());
        assertEquals(expectedResponse.roles().length, actualResponse.roles().length);
        assertEquals(Arrays.stream(expectedResponse.roles()).findFirst(), Arrays.stream(actualResponse.roles()).findFirst());
        assertEquals(expectedResponse.fromAvailable(), actualResponse.fromAvailable());
        assertEquals(expectedResponse.toAvailable(), actualResponse.toAvailable());

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
    }

    @Test
    void getAppUserFullData_Throws_UserNotFoundException() {
        when(repository.findAppUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrowsExactly(UsernameNotFoundException.class,
                () -> appUserService.getAppUserFullData("someEmail"),
                ErrorMessage.USER_NOT_FOUND.toString());
        verify(repository, times(1)).findAppUserByEmail(anyString());
    }


    @Test
    void updateUserData_All_Fields_Filled_Success() {
        UserDataUpdateRequest request = new UserDataUpdateRequest("newName", "newUrl");

        var changedUser = new AppUser(user1.getId(), "newName",
                user1.getEmail(), user1.getPassword(), "newUrl",
                user1.getRoles(), user1.getAvailability());

        AppUserFullResponse expectedResponse = new AppUserFullResponse(changedUser);

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(repository.save(changedUser)).thenReturn(changedUser);

        AppUserFullResponse actualResponse = appUserService.updateUserData(request, user1.getEmail());

        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.availability(), actualResponse.availability());
        assertEquals(expectedResponse.roles().length, actualResponse.roles().length);
        assertEquals(Arrays.stream(expectedResponse.roles()).findFirst(), Arrays.stream(actualResponse.roles()).findFirst());
        assertEquals(expectedResponse.fromAvailable(), actualResponse.fromAvailable());
        assertEquals(expectedResponse.toAvailable(), actualResponse.toAvailable());

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(1)).save(changedUser);
    }

    @Test
    void updateUserData_Throws_All_Fields_Empty() {
        UserDataUpdateRequest request = new UserDataUpdateRequest("", "");
        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));


        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserService.updateUserData(request, user1.getEmail()), "Both request fields are empty");

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(0)).save(user1);
    }

    @Test
    void updateUserData_Throws_All_Fields_Identical_To_Actual_State() {
        UserDataUpdateRequest request = new UserDataUpdateRequest(user1.getName(), user1.getAvatarUrl());
        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserService.updateUserData(request, user1.getEmail()), "Request fields are identical to User actual state");

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(0)).save(user1);
    }

    @Test
    void updateUserAvailability() {

        UserAvailabilityUpdateRequest request = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.UNAVAILABLE.toString(),
                "12:00:00+01:00", "16:00:00+01:00");

        String expectedFromUnavailable = timeToStringFormatter(user1.getAvailability().getFromUnavailable());
        String expectedToUnavailable = timeToStringFormatter(user1.getAvailability().getToUnavailable());

        String[] userRoles = user1.getRoles().stream().map(AppUserRole::getName).toArray(String[]::new);

        AppUserFullResponse expectedResponse = new AppUserFullResponse(user1.getId(), user1.getName(),
                user1.getEmail(), user1.getAvatarUrl(), userRoles, request.newAvailability(),
                request.newFromUnavailable(), request.newToUnavailable());

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(repository.save(user1)).thenReturn(user1);

        AppUserFullResponse actualResponse = appUserService.updateUserAvailability(request, user1.getEmail());

        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.availability(), actualResponse.availability());
        assertEquals(expectedResponse.roles().length, actualResponse.roles().length);
        assertEquals(expectedFromUnavailable, actualResponse.fromAvailable());
        assertEquals(expectedToUnavailable, actualResponse.toAvailable());

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(1)).save(user1);
    }

    @Test
    void updateUserAvailability_Temporary_Unavailable() {

        UserAvailabilityUpdateRequest request = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.TEMPORARILY_UNAVAILABLE.toString(),
                "12:00:00+01:00", "16:00:00+01:00");

        String[] userRoles = user1.getRoles().stream().map(AppUserRole::getName).toArray(String[]::new);

        AppUserFullResponse expectedResponse = new AppUserFullResponse(user1.getId(), user1.getName(),
                user1.getEmail(), user1.getAvatarUrl(), userRoles, request.newAvailability(),
                request.newFromUnavailable(), request.newToUnavailable());

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(repository.save(user1)).thenReturn(user1);

        AppUserFullResponse actualResponse = appUserService.updateUserAvailability(request, user1.getEmail());

        assertEquals(expectedResponse.userId(), actualResponse.userId());
        assertEquals(expectedResponse.email(), actualResponse.email());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.availability(), actualResponse.availability());
        assertEquals(expectedResponse.roles().length, actualResponse.roles().length);
        assertEquals(expectedResponse.fromAvailable(), actualResponse.fromAvailable());
        assertEquals(expectedResponse.toAvailable(), actualResponse.toAvailable());

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(1)).save(user1);
    }

    @Test
    void updateUserAvailability_Throws_Wrong_TimeString_Format() {

        UserAvailabilityUpdateRequest request1 = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.TEMPORARILY_UNAVAILABLE.toString(),
                "14:00:00+01:00", "25:00:00+01:00");

        UserAvailabilityUpdateRequest request2 = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.TEMPORARILY_UNAVAILABLE.toString(),
                "14:00:00+01:00", "23:60:00+01:00");

        UserAvailabilityUpdateRequest request3 = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.TEMPORARILY_UNAVAILABLE.toString(),
                "14:00:00+01:00", "23:59:60+01:00");

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        assertThrowsExactly(IllegalArgumentException.class, () -> appUserService.updateUserAvailability(request1, user1.getEmail()));
        assertThrowsExactly(IllegalArgumentException.class, () -> appUserService.updateUserAvailability(request2, user1.getEmail()));
        assertThrowsExactly(IllegalArgumentException.class, () -> appUserService.updateUserAvailability(request3, user1.getEmail()));

        verify(repository, times(0)).save(user1);
    }

    @Test
    void updateUserAvailability_Throws_To_Before_After() {

        UserAvailabilityUpdateRequest request1 = new UserAvailabilityUpdateRequest(
                AvailabilityEnum.TEMPORARILY_UNAVAILABLE.toString(),
                "14:00:00+01:00", "12:00:00+01:00");

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserService.updateUserAvailability(request1, user1.getEmail()),
                "To time must be after from");

        verify(repository, times(0)).save(user1);
    }

    @Test
    void updateUserAvailability_Throws_AvailabilityString_NotValid() {

        UserAvailabilityUpdateRequest request = new UserAvailabilityUpdateRequest(
                "NotValidString",
                "12:00:00+01:00", "16:00:00+01:00");

        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserService.updateUserAvailability(request, user1.getEmail()),
                "The new Availability do not meet the AvailabilityEnum");

        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
        verify(repository, times(0)).save(user1);
    }

    @Test
    void getUserByEmailOrThrow() {
        when(repository.findAppUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

        AppUser result = appUserService.getUserByEmailOrThrow(user1.getEmail());

        assertEquals(user1, result);
        verify(repository, times(1)).findAppUserByEmail(user1.getEmail());
    }

    @Test
    void getUserByEmailOrThrow_Throws_UserNotFoundException() {
        when(repository.findAppUserByEmail(anyString())).thenReturn(Optional.empty());
        assertThrowsExactly(UsernameNotFoundException.class,
                () -> appUserService.getUserByEmailOrThrow("someEmail"),
                ErrorMessage.USER_NOT_FOUND.toString());
        verify(repository, times(1)).findAppUserByEmail(anyString());
    }

    @Test
    void getUsersListByIds() {

        when(repository.findAllById(listOf(user1.getId(), user2.getId()))).thenReturn(listOf(user1, user2));

        List<AppUser> result = appUserService.getUsersListByIds(listOf(user1.getId(), user2.getId()));

        assertEquals(listOf(user1, user2), result);
        verify(repository, times(1)).findAllById(listOf(user1.getId(), user2.getId()));
    }

    @Test
    void getUsersListByIds_Throws_Request_Empty() {

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserService.getUsersListByIds(listOf()),
                "List of users is empty");

        verify(repository, times(0)).findAllById(listOf(user1.getId(), user2.getId()));
    }

    @Test
    void getUsersListByIds_Throws_EmptyList_Received() {

        List<Long> ids = listOf(6L, 9L);

        List<AppUser> emptyList = new ArrayList<>();

        when(repository.findAllById(ids)).thenReturn(emptyList);

        assertThrowsExactly(UsernameNotFoundException.class,
                () -> appUserService.getUsersListByIds(ids),
                ErrorMessage.USER_NOT_FOUND.toString());

        verify(repository, times(1)).findAllById(ids);
    }

    @Test
    void getUsersListByIds_Throws_ShorterList_Received() {

        List<Long> ids = listOf(1L, 2L, 3L);

        List<AppUser> receivedList = List.of(user1, user2);

        when(repository.findAllById(ids)).thenReturn(receivedList);

        assertThrowsExactly(UsernameNotFoundException.class,
                () -> appUserService.getUsersListByIds(ids),
                "Request contains " + (ids.size() - receivedList.size()) + " invalid users Ids");

        verify(repository, times(1)).findAllById(ids);
    }
}