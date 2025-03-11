package org.lets_play_be.service.mappers;

import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.AppUserRole;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.stereotype.Service;

@Service
public class AppUserMappers {
    public AppUserFullResponse toFullUserResponse(AppUser appUser) {

        String[] roles = getUserRoles(appUser);
        //can be null
        String fromAvailable = appUser.getAvailability().getFromUnavailable() == null ? null : FormattingUtils.TIME_TO_STRING_FORMATTER(appUser.getAvailability().getFromUnavailable());
        //can be null
        String toAvailable = appUser.getAvailability().getToUnavailable() == null ? null : FormattingUtils.TIME_TO_STRING_FORMATTER(appUser.getAvailability().getToUnavailable());

        return new AppUserFullResponse(appUser.getId(),
                appUser.getName(), appUser.getEmail(),
                appUser.getAvatarUrl(), roles, appUser.getAvailability().getAvailabilityType().toString(),
                fromAvailable, toAvailable);
    }

    public UserShortResponse toUserShortResponse(AppUser appUser) {
        return new UserShortResponse(appUser.getId(), appUser.getName());
    }


    private String[] getUserRoles(AppUser appUser) {
        return appUser.getRoles()
                .stream()
                .map(AppUserRole::getName)
                .toArray(String[]::new);
    }
}
