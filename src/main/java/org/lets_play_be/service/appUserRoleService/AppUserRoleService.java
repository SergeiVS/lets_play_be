package org.lets_play_be.service.appUserRoleService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.entity.user.AppUserRole;

import org.lets_play_be.service.initialization.AppUserRoleMapping;
import org.springframework.stereotype.Service;

import static org.lets_play_be.entity.enums.UserRoleEnum.findRole;

@Service
@RequiredArgsConstructor
public class AppUserRoleService {

    private final AppUserRoleMapping mapping;


    public AppUserRole getRoleByNameOrThrow(String roleName) {

       UserRoleEnum role = findRole(roleName).orElseThrow(() -> new IllegalArgumentException("Invalid role name: " + roleName));

       return mapping.get(role);
    }

}
