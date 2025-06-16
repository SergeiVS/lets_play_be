package org.lets_play_be.service.appUserRoleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.service.initialization.AppUserRoleMapping;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.when;


@Profile("test")
@ExtendWith(MockitoExtension.class)
class AppUserRoleServiceTest {

    @Mock
    private AppUserRoleMapping mapping;

    @InjectMocks
    private AppUserRoleService appUserRoleService;

    AppUserRole roleAdmin;

    @BeforeEach
    void setUp() {
        roleAdmin = new AppUserRole(1L, UserRoleEnum.ROLE_ADMIN.name(), new ArrayList<>());
    }

    @AfterEach
    void tearDown() {
        roleAdmin = null;
    }


    @Test
    void getRoleByNameOrThrowPositive() {
        when(mapping.get(UserRoleEnum.ROLE_ADMIN)).thenReturn(roleAdmin);


        AppUserRole result1 = appUserRoleService.getRoleByNameOrThrow("ROLE_ADMIN");
        AppUserRole result2 = appUserRoleService.getRoleByNameOrThrow("role_admin");
        AppUserRole result3 = appUserRoleService.getRoleByNameOrThrow("Role_admin");

        assertThat(result1).isEqualTo(roleAdmin);
        assertThat(result2).isEqualTo(roleAdmin);
        assertThat(result3).isEqualTo(roleAdmin);

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserRoleService.getRoleByNameOrThrow("admin"),
                "Invalid role name: admin");

        assertThrowsExactly(IllegalArgumentException.class,
                () -> appUserRoleService.getRoleByNameOrThrow("ROLE_FALSEADMIN"),
                "Invalid role name: ROLE_FALSEADMIN");
    }
}