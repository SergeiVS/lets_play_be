package org.lets_play_be.service.appUserService;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.AppUserRepository;
import org.lets_play_be.service.appUserRoleService.AppUserRoleService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lets_play_be.utils.FormattingUtils.timeToStringFormatter;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class RegisterNewUserServiceTest {

    @Mock
    private AppUserRoleService roleServiceMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private AppUserRepository repositoryMock;
    @InjectMocks
    private RegisterNewUserService registerNewUserService;

    private AppUserFullResponse responseMocked;
    private NewUserRegistrationRequest request;
    private UserAvailability availability;
    private AppUser appUserForSave;
    private AppUser appUserSaved;
    private AppUserRole role;


    @BeforeEach
    void setUp() {
        request = new NewUserRegistrationRequest("Name", "name@testemail.com", "password", "");
        availability = new UserAvailability(AvailabilityEnum.AVAILABLE);

        String name = request.name();
        String email = request.email();
        String password = "hashedPassword";
        String avatarUrl = "N/A";
        String unavailableFrom = timeToStringFormatter(availability.getUnavailableFrom());
        String unavailableTo = timeToStringFormatter(availability.getUnavailableTo());

        role = new AppUserRole(UserRoleEnum.ROLE_USER.name());
        responseMocked = new AppUserFullResponse(1L, "Name", "name@testemail.com", avatarUrl,
                new String[]{role.getName()}, "AVAILABLE", unavailableFrom, unavailableTo);
        appUserForSave = new AppUser(name, email, password, avatarUrl);
        appUserForSave.getRoles().add(role);
        appUserForSave.setAvailability(availability);
        appUserSaved = new AppUser(1L, name, email, password, avatarUrl);
        appUserSaved.getRoles().add(role);
        appUserSaved.setAvailability(availability);
    }

    @AfterEach
    void tearDown() {
        responseMocked = null;
        request = null;
        availability = null;
        appUserForSave = null;
        appUserSaved = null;
        role = null;
    }

    @Test
    void registerNewUserPositive() {

        when(repositoryMock.existsByEmail("name@testemail.com")).thenReturn(false);
        when(repositoryMock.existsByName("Name")).thenReturn(false);
        when(passwordEncoderMock.encode("password")).thenReturn("hashedPassword");
        when(roleServiceMock.getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name())).thenReturn(role);
        when(repositoryMock.save(appUserForSave)).thenReturn(appUserSaved);

        AppUserFullResponse result = registerNewUserService.registerNewUser(request);

        assertThat(result.userId()).isEqualTo(responseMocked.userId());
        assertThat(result.name()).isEqualTo(responseMocked.name());
        assertThat(result.email()).isEqualTo(responseMocked.email());
        assertThat(result.avatarUrl()).isEqualTo(responseMocked.avatarUrl());
        assertThat(result.roles().length).isEqualTo(1);
        assertThat(result.roles()[0]).isEqualTo(role.getName());
        assertThat(result.availability()).isEqualTo(responseMocked.availability());
        assertThat(result.unavailableFrom()).isEqualTo(responseMocked.unavailableFrom());
//        assertThat(result.unavailableTo()).isEqualTo(responseMocked.unavailableTo());

        verify(repositoryMock, times(1)).existsByEmail("name@testemail.com");
        verify(repositoryMock, times(1)).existsByName("Name");
        verify(passwordEncoderMock, times(1)).encode("password");
        verify(roleServiceMock, times(1)).getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());
        verify(repositoryMock, times(1)).save(appUserForSave);
    }


    @Test
    void registerNewUser_throws_RestException_EmailAlreadyExists() {

        when(repositoryMock.existsByEmail("name@testemail.com")).thenReturn(true);

        assertThrows(RestException.class, () -> registerNewUserService.registerNewUser(request));
        verify(repositoryMock, times(1)).existsByEmail("name@testemail.com");
        verify(repositoryMock, times(0)).existsByName("Name");
        verify(passwordEncoderMock, times(0)).encode("password");
        verify(roleServiceMock, times(0)).getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());
        verify(repositoryMock, times(0)).save(appUserForSave);
    }

    @Test
    void registerNewUser_throws_RestException_NameAlreadyExists() {

        when(repositoryMock.existsByEmail("name@testemail.com")).thenReturn(false);
        when(repositoryMock.existsByName("Name")).thenReturn(true);

        assertThrows(RestException.class, () -> registerNewUserService.registerNewUser(request));
        verify(repositoryMock, times(1)).existsByEmail("name@testemail.com");
        verify(repositoryMock, times(1)).existsByName("Name");
        verify(passwordEncoderMock, times(0)).encode("password");
        verify(roleServiceMock, times(0)).getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());
        verify(repositoryMock, times(0)).save(appUserForSave);
    }
}