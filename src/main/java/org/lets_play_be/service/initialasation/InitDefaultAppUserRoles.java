package org.lets_play_be.service.initialasation;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.AppUserRole;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.repository.AppUserRoleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitDefaultAppUserRoles {
    private final AppUserRoleRepository repository;
    private final AppUserRoleMapping appUserRoleMapping;

    @PostConstruct
    public void init() {
        for (UserRoleEnum roleEnum : UserRoleEnum.values()) {
            Optional<AppUserRole> roleByName = repository.findByNameIgnoreCase(roleEnum.name());
            if (roleByName.isPresent()) {
                AppUserRole role = roleByName.get();
                appUserRoleMapping.put(roleEnum, role);
            } else {
                AppUserRole role = new AppUserRole(roleEnum.name());
                AppUserRole savedRole = repository.save(role);
                appUserRoleMapping.put(roleEnum, savedRole);
            }
        }

    }


}
