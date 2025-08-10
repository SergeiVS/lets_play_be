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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;
import static org.lets_play_be.utils.ValidationUtils.isFromTimeBeforeTo;
import static org.lets_play_be.utils.ValidationUtils.validateAvailabilityString;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;

    public List<AppUserFullResponse> getAllUsers() {
        var users = userRepository.findAll();

        return users.stream().map(AppUserFullResponse::new).toList();
    }

    public AppUserFullResponse getAppUserFullData(String email) {
        AppUser user = getUserByEmailOrThrow(email);
        return new AppUserFullResponse(user);
    }

    @Transactional
    public AppUserFullResponse updateUserData(UserDataUpdateRequest request, String email) {
        AppUser user = getUserByEmailOrThrow(email);

        if (user.getName().equals(request.newName()) && user.getAvatarUrl().equals(request.newAvatarUrl())) {
            throw new IllegalArgumentException("Request fields are identical to User actual state");
        }

        if (request.newName().isEmpty() && request.newAvatarUrl().isEmpty()) {
            throw new IllegalArgumentException("Both request fields are empty");
        }

        setNewNameToUser(request, user);
        setNewAvatarUrlToUser(request, user);

        AppUser savedUser = userRepository.save(user);
        return new AppUserFullResponse(savedUser);
    }

    @Transactional
    public AppUserFullResponse updateUserAvailability(UserAvailabilityUpdateRequest request, String email) {

        AppUser user = getUserByEmailOrThrow(email);

        setNewAvailability(request, user);

        AppUser savedUser = userRepository.save(user);

        return new AppUserFullResponse(savedUser);
    }

    public AppUser getUserByEmailOrThrow(String email) {
        return userRepository.findAppUserByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString()));
    }

    public List<AppUser> getUsersListByIds(List<Long> ids) {

        if (ids.isEmpty()) {
            throw new IllegalArgumentException("List of users is empty");
        }

        List<AppUser> users = userRepository.findAllById(ids);

        if (users.isEmpty()) {
            throw new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.toString());
        }

        if (users.size() != ids.size()) {
            throw new UsernameNotFoundException("Request contains " + (ids.size() - users.size()) + " invalid users Ids");
        }
        return users;
    }

    private void setNewAvailability(UserAvailabilityUpdateRequest request, AppUser user) {

        UserAvailability availability = user.getAvailability();

        String availabilityString = request.newAvailability();

        validateAvailabilityString(availabilityString);

        availability.setAvailabilityType(AvailabilityEnum.valueOf(availabilityString.toUpperCase()));

        setTemporaryUnavailabilityTime(request, availability);

        user.setAvailability(availability);
    }

    private void setTemporaryUnavailabilityTime(UserAvailabilityUpdateRequest request, UserAvailability availability) {

        if (availability.getAvailabilityType().equals(AvailabilityEnum.TEMPORARILY_UNAVAILABLE)) {

            OffsetTime unavailableFrom = timeStringToOffsetTime(request.newUnavailableFrom());
            OffsetTime unavailableTo = timeStringToOffsetTime(request.newUnavailableTo());

            isFromTimeBeforeTo(unavailableFrom, unavailableTo);

            availability.setUnavailableFrom(unavailableFrom);
            availability.setUnavailableTo(unavailableTo);
        }
    }

    private void setNewAvatarUrlToUser(UserDataUpdateRequest request, AppUser user) {
        if (!request.newAvatarUrl().isEmpty() && !request.newAvatarUrl().equals(user.getAvatarUrl())) {
            user.setAvatarUrl(request.newAvatarUrl());
        }
    }

    private void setNewNameToUser(UserDataUpdateRequest request, AppUser user) {
        if (!request.newName().isEmpty() && !request.newName().equals(user.getName())) {
            user.setName(request.newName());
        }
    }
}
