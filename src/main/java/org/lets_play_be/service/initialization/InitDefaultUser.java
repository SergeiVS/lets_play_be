package org.lets_play_be.service.initialization;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.repository.UserAvailabilityRepository;
import org.lets_play_be.service.appUserService.AppUserRepositoryService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.lets_play_be.service.mappers.AppUserMappers.getTimeFormatter;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
@DependsOn("initDefaultAppUserRoles")
public class InitDefaultUser {
    private final AppUserRepositoryService repositoryService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRoleMapping appUserRoleMapping;
    private final UserAvailabilityRepository availabilityRepository;

    @PostConstruct
    @Transactional
    public void init() {
        List<AppUser> defaultUsers = getListOfDefaultUsers();

        for (AppUser user : defaultUsers) {
            if (!repositoryService.existsByEmail(user.getEmail())) {

                UserAvailability availability = new UserAvailability(AvailabilityEnum.AVAILABLE);
                availability.setFromUnavailable(OffsetTime.parse("000000+0000", getTimeFormatter()));
                availability.setToUnavailable(OffsetTime.parse("000000+0000", getTimeFormatter()));
                user.setAvailability(availability);
                AppUser savedUser = repositoryService.save(user);

                log.info("Saved user: {}", savedUser.getEmail());
            }
        }
    }

    private List<AppUser> getListOfDefaultUsers() {
        List<AppUser> defaultUsers = new ArrayList<>();

        AppUser testUserSerge = getTestUser("USerge",
                "labrary.test@gmail.com", "User@Test1", "N/A",
                new UserRoleEnum[]{UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_USER});

        AppUser testUserPavel = getTestUser("Pavel",
                "pavel@testemail.com", "User@Test1", "N/A",
                new UserRoleEnum[]{UserRoleEnum.ROLE_ADMIN, UserRoleEnum.ROLE_USER});

        defaultUsers.add(testUserSerge);
        defaultUsers.add(testUserPavel);

        return defaultUsers;
    }

    private AppUser getTestUser(String name, String email, String password, String avatarUrl, UserRoleEnum[] roleNames) {

        String passwordHash = passwordEncoder.encode(password);
        AppUser testUser = new AppUser(name, email, passwordHash, avatarUrl);
        testUser.setRoles(getAppUserRoles(roleNames));

        return testUser;
    }

    private List<AppUserRole> getAppUserRoles(UserRoleEnum[] roleNames) {

        List<AppUserRole> userRoles = new ArrayList<>();
        for (UserRoleEnum roleName : roleNames) {
            AppUserRole appUserRole = appUserRoleMapping.get(roleName);
            userRoles.add(appUserRole);
        }
        return userRoles;
    }
}
