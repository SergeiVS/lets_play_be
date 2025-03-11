package org.lets_play_be.service.appUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.dto.userDto.AppUserFullResponse;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.AppUserRole;
import org.lets_play_be.entity.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.UserRoleEnum;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.UserAvailabilityRepository;
import org.lets_play_be.service.appUserRoleService.AppUserRoleService;
import org.lets_play_be.service.mappers.AppUserMappers;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.lets_play_be.utils.FormattingUtils.NORMALIZE_EMAIL;

@Service
@RequiredArgsConstructor
public class RegisterNewUserService {

    private final AppUserRepositoryService repositoryService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMappers mapper;
    private final AppUserRoleService roleService;
    private final UserAvailabilityRepository availabilityRepository;

    @Transactional
    public AppUserFullResponse registerNewUser(NewUserRegistrationRequest request) {

        isUserExistByEmail(request.email());
        isUserExistByName(request.name());

        AppUser userForSave = getUserForSave(request);
        AppUser savedUser = repositoryService.save(userForSave);

        return mapper.toFullUserResponse(savedUser);
    }

    private AppUser getUserForSave(NewUserRegistrationRequest request) {

        String name = request.name();
        String email = NORMALIZE_EMAIL(request.email());
        String password = passwordEncoder.encode(request.password().trim());
        String avatarUrl = (request.avatarUrl().isEmpty()) ? "N/A" : request.avatarUrl().trim();
        AppUserRole role = roleService.getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());
        UserAvailability availability = availabilityRepository.save(new UserAvailability(AvailabilityEnum.AVAILABLE));

        AppUser userForSave = new AppUser(name, email, password, avatarUrl);
        userForSave.setAvailability(availability);
        userForSave.getRoles().add(role);
        return userForSave;
    }

    private void isUserExistByEmail(String email) {
        if (repositoryService.existsByEmail(email)) {
            throw new RestException(ErrorMessage.USER_ALREADY_EXISTS.toString(), HttpStatus.CONFLICT);
        }
    }

    private void isUserExistByName(String name) {
        if (repositoryService.existsByName(name)) {
            throw new RestException(ErrorMessage.USER_ALREADY_EXISTS.toString(), HttpStatus.CONFLICT);
        }
    }
}
