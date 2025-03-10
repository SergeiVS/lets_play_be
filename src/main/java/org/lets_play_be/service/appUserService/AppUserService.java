package org.lets_play_be.service.appUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.repository.UserAvailabilityRepository;
import org.lets_play_be.service.mappers.AppUserMappers;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;

import static org.lets_play_be.utils.FormattingUtils.convertStringToLocalTime;
import static org.lets_play_be.utils.ValidationUtils.validateAvailabilityString;
import static org.lets_play_be.utils.ValidationUtils.validateTimeOptionByTemp_Av;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepositoryService userRepositoryService;
    private final AppUserMappers userMappers;
    private final UserAvailabilityRepository availabilityRepository;

    public AppUserFullResponse getAppUserFullData(String email) {
        AppUser user = getUserByEmailOrThrow(email);
        return userMappers.toFullUserResponse(user);
    }

    @Transactional
    public AppUserFullResponse updateUserNameAndAvatarUrl(UserDataUpdateRequest request, String email) {
        AppUser user = getUserByEmailOrThrow(email);
        validateUserInRequest(request.userId(), user);
        setNewNameToUser(request, user);
        setNeAvatarUrlToUser(request, user);
        AppUser savedUser = userRepositoryService.save(user);
        return userMappers.toFullUserResponse(savedUser);
    }

    @Transactional
    public AppUserFullResponse updateUserAvailability(UserAvailabilityUpdateRequest request, String email) {
        AppUser user = getUserByEmailOrThrow(email);
        validateUserInRequest(request.userId(), user);
        setNewAvailability(request, user);
        AppUser savedUser = userRepositoryService.save(user);
        return userMappers.toFullUserResponse(savedUser);
    }

    private void setNewAvailability(UserAvailabilityUpdateRequest request, AppUser user) {
        UserAvailability availability = user.getAvailability();
        String availabilityString = request.newAvailability();
        OffsetTime fromAvailable = convertStringToLocalTime(request.newFromUnavailable());
        OffsetTime toAvailable = convertStringToLocalTime(request.newToUnavailable());


        validateAvailabilityString(availabilityString);

        availability.setAvailabilityType(AvailabilityEnum.valueOf(availabilityString.toUpperCase()));
        validateTimeOptionByTemp_Av(availability, fromAvailable, toAvailable);
        availability.setFromUnavailable(fromAvailable);
        availability.setToUnavailable(toAvailable);
        UserAvailability savedAvailability = availabilityRepository.save(availability);
        user.setAvailability(savedAvailability);
    }

    private void setNeAvatarUrlToUser(UserDataUpdateRequest request, AppUser user) {
        if (request.newAvatarUrl() != null && !request.newAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.newAvatarUrl());
        }
    }

    private void setNewNameToUser(UserDataUpdateRequest request, AppUser user) {
        if (request.newName() != null && !request.newName().isEmpty()) {
            user.setName(request.newName());
        }
    }

    private void validateUserInRequest(Long userIdFromRequest, AppUser user) {
        if (!user.getId().equals(userIdFromRequest)) {
            throw new IllegalStateException("User userId from request not match id of Principal");
        }

    }

    private AppUser getUserByEmailOrThrow(String email) {
        return userRepositoryService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));
    }


}
