package org.lets_play_be.service.initialization;

import org.lets_play_be.entity.user.AppUserRole;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppUserRoleMapping {

    private final Map<UserRoleEnum, AppUserRole> userRoleMap = new ConcurrentHashMap<>(UserRoleEnum.values().length);

    void put(UserRoleEnum roleEnum, AppUserRole appUserRole){
        userRoleMap.put(roleEnum, appUserRole);
    }

    public AppUserRole get(UserRoleEnum roleEnum){
        return userRoleMap.get(roleEnum);
    }
}
