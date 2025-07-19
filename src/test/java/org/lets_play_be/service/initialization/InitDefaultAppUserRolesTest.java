package org.lets_play_be.service.initialization;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.repository.AppUserRoleRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitDefaultAppUserRolesTest {

    @Mock
    AppUserRoleRepository repositoryMock;
    @Mock
    AppUserRoleMapping mappingMock;
    @InjectMocks
    InitDefaultAppUserRoles initDefaultAppUserRoles;

    AppUserRole roleAdmin;
    AppUserRole roleUser;
    AppUserRole roleUserForSave;

    @BeforeEach
    void setUp() {
        roleAdmin = new AppUserRole(1L, UserRoleEnum.ROLE_ADMIN.name());
        roleUserForSave = new AppUserRole(UserRoleEnum.ROLE_USER.name());
        roleUser = new AppUserRole(2L, UserRoleEnum.ROLE_USER.name());
    }

    @AfterEach
    void tearDown() {
        roleAdmin = null;
        roleUser = null;
    }

    @Test
    void init() {
        lenient().when(repositoryMock.findByNameIgnoreCase(UserRoleEnum.ROLE_ADMIN.name())).thenReturn(Optional.ofNullable(roleAdmin));
        lenient().when(repositoryMock.findByNameIgnoreCase(UserRoleEnum.ROLE_USER.name())).thenReturn(Optional.empty());
        lenient().when(repositoryMock.save(roleUserForSave)).thenReturn(roleUser);
        doNothing().when(mappingMock).put(any(UserRoleEnum.class), any(AppUserRole.class));

        initDefaultAppUserRoles.init();

        verify(repositoryMock, times(2)).findByNameIgnoreCase(anyString());
        verify(repositoryMock, times(1)).save(new AppUserRole(UserRoleEnum.ROLE_USER.name()));
        verify(mappingMock, times(2)).put(any(UserRoleEnum.class), any(AppUserRole.class));
    }
}