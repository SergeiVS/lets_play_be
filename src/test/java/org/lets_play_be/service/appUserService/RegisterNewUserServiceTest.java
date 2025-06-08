package org.lets_play_be.service.appUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.repository.UserAvailabilityRepository;
import org.lets_play_be.service.appUserRoleService.AppUserRoleService;
import org.lets_play_be.service.mappers.AppUserMappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterNewUserServiceTest {

    @Mock
    private AppUserRepositoryService userRepositoryService;
    @Mock
    private UserAvailabilityRepository availabilityRepository;
    @Mock
    private AppUserRoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoderMock;
    @Mock
    private AppUserMappers mappers;
    @InjectMocks
    private RegisterNewUserService registerNewUserService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final AppUserMappers appUserMappers = new AppUserMappers();

    private AppUserFullResponse responseMocked;

    private NewUserRegistrationRequest request;

    private UserAvailability availability;

    private AppUser appUserForSave;

    private AppUser appUserSaved;

    private AppUserRole role;

    String name;
    String email;
    String password;
    String avatarUrl;
    String fromAvailable;
    String toAvailable;


    @BeforeEach
    void setUp() {
        request = new NewUserRegistrationRequest("Name", "name@testemail.com", "@Password1", "");
        when(passwordEncoderMock.encode(any(String.class))).thenReturn(new BCryptPasswordEncoder().encode("@Password1"));

        availability = new UserAvailability(AvailabilityEnum.AVAILABLE);
        availability.setId(1L);
        availability.setFromUnavailable(OffsetTime.parse("00:00+00:00"));
        availability.setToUnavailable(OffsetTime.parse("00:00+00:00"));

        responseMocked = new AppUserFullResponse(1L, "Name", "name@testemail.com", avatarUrl,
                new String[]{"ROLE_USER"}, "AVAILABLE", fromAvailable, toAvailable);

        fromAvailable = availability.getFromUnavailable().toString();
        toAvailable = availability.getToUnavailable().toString();

        name = request.name();
        email = request.email();
        password = passwordEncoder.encode(request.password());
        avatarUrl = "N/A";
        role = roleService.getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());


        appUserForSave = new AppUser(name, email, password, avatarUrl);
        appUserForSave.getRoles().add(role);
        appUserForSave.setAvailability(availability);

        appUserSaved = new AppUser(name, email, password, avatarUrl);
        appUserSaved.setId(1L);
        appUserSaved.getRoles().add(role);
        appUserSaved.setAvailability(availability);

    }

    //    @ParameterizedTest
    @Test
    void registerNewUserPositive() {

//        when(mappers.toFullUserResponse(appUserSaved)).thenReturn(responseMocked);
//        when(userRepositoryService.existsByEmail("name@testemail.com")).thenReturn(false);
//        when(userRepositoryService.existsByName("Name")).thenReturn(false);
//        when(availabilityRepository.save(any(UserAvailability.class))).thenReturn(availability);
//        when(roleService.getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name())).thenReturn(role);
//        when(userRepositoryService.save(appUserForSave)).thenReturn(appUserSaved);
//
//        AppUserFullResponse response = mappers.toFullUserResponse(appUserSaved);
//        AppUserFullResponse result = registerNewUserService.registerNewUser(request);
//
//        assertThat(result).isEqualTo(response);


    }

}