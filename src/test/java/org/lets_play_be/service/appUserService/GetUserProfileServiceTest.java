package org.lets_play_be.service.appUserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.repository.AppUserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserProfileServiceTest {

    @Mock
    AppUserRepository repository;

    @InjectMocks
    GetUserProfileService service;

    AppUser user;

    AppUserRole role;

    @BeforeEach
    void setUp() {
        role = new AppUserRole(1L, UserRoleEnum.ROLE_USER.name());
        user = new AppUser(1L, "User1", "email@email.com", "password", "");
        user.getRoles().add(role);
    }

    @AfterEach
    void tearDown() {
        user = null;
        role = null;
    }

    @Test
    void getUserProfile() {
        when(repository.findAppUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        AppUserProfile expectedProfile = new AppUserProfile(user);
        AppUserProfile result = service.getUserProfile(user.getEmail());

        assertEquals(expectedProfile, result);
        verify(repository, times(1)).findAppUserByEmail(user.getEmail());
    }

    @Test
    void getUserProfile_Throws_UserNotFound() {
        when(repository.findAppUserByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrowsExactly(UsernameNotFoundException.class,
                () -> service.getUserProfile(user.getEmail()),
                "The user with email: " + user.getEmail() + " not found");
        verify(repository, times(1)).findAppUserByEmail(user.getEmail());
    }
}