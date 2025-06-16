package org.lets_play_be.service.appUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.dto.userDto.UserAvailabilityUpdateRequest;
import org.lets_play_be.dto.userDto.UserDataUpdateRequest;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.repository.AppUserRepository;
import org.lets_play_be.service.UserAvailabilityService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;
import static org.lets_play_be.utils.ValidationUtils.validateAvailabilityString;
import static org.lets_play_be.utils.ValidationUtils.validateTimeOptionByTemp_Av;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final UserAvailabilityService availabilityService;

    public AppUserFullResponse getAppUserFullData(String email) {

        AppUser user = getUserByEmailOrThrow(email);
        return new AppUserFullResponse(user);
    }

    @Transactional
    public AppUserFullResponse updateUserData(UserDataUpdateRequest request, String email) {
        AppUser user = getUserByEmailOrThrow(email);
        validateUserInRequest(request.userId(), user);
        setNewNameToUser(request, user);
        setNewAvatarUrlToUser(request, user);
        AppUser savedUser = userRepository.save(user);
        return new AppUserFullResponse(savedUser);
    }

    @Transactional
    public AppUserFullResponse updateUserAvailability(UserAvailabilityUpdateRequest request, String email) {
        AppUser user = getUserByEmailOrThrow(email);
        validateUserInRequest(request.userId(), user);
        setNewAvailability(request, user);
        AppUser savedUser = userRepository.save(user);
        return new AppUserFullResponse(savedUser);
    }

    public AppUser getUserByEmailOrThrow(String email) {
        return userRepository.findAppUserByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));
    }

    public List<AppUser> getUsersListByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public Long getUserIdByEmailOrThrow(String email) {
        return userRepository.findAppUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()))
                .getId();
    }

    private void setNewAvailability(UserAvailabilityUpdateRequest request, AppUser user) {
        UserAvailability availability = user.getAvailability();
        String availabilityString = request.newAvailability();
        OffsetTime fromAvailable = timeStringToOffsetTime(request.newFromUnavailable());
        OffsetTime toAvailable = timeStringToOffsetTime(request.newToUnavailable());


        validateAvailabilityString(availabilityString);

        availability.setAvailabilityType(AvailabilityEnum.valueOf(availabilityString.toUpperCase()));
        validateTimeOptionByTemp_Av(availability, fromAvailable, toAvailable);
        availability.setFromUnavailable(fromAvailable);
        availability.setToUnavailable(toAvailable);
        UserAvailability savedAvailability = availabilityService.saveAvailability(availability);
        user.setAvailability(savedAvailability);
    }

    private void setNewAvatarUrlToUser(UserDataUpdateRequest request, AppUser user) {
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
            throw new IllegalStateException("User usersId from request not match id of Principal");
        }

    }


}
