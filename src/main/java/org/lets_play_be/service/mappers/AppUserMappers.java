package org.lets_play_be.service.mappers;

import org.lets_play_be.dto.userDto.NewUserRegistrationResponse;
import org.lets_play_be.entity.AppUser;
import org.springframework.stereotype.Service;

@Service
public class AppUserMappers {
    public NewUserRegistrationResponse toNewUserResponse(AppUser appUser) {
        return new NewUserRegistrationResponse(appUser.getId(),
                appUser.getName(), appUser.getEmail(),
                appUser.getAvatarUrl(), appUser.getAvailability().toString());
    }
}
