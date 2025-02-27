package org.lets_play_be.service.appUserRoleService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.AppUserRole;
import org.lets_play_be.repository.AppUserRoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserRoleService {

    private final AppUserRoleRepository appUserRoleRepository;

    public AppUserRole getRoleByNameOrThrow(String roleName) {
       return appUserRoleRepository.findByNameIgnoreCase(roleName)
               .orElseThrow(()-> new RuntimeException("Role with name " + roleName + " not found"));
    }

}
