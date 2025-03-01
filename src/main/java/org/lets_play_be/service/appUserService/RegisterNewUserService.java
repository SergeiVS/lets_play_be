package org.lets_play_be.service.appUserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.common.ErrorMessage;
import org.lets_play_be.dto.userDto.NewUserRegistrationRequest;
import org.lets_play_be.dto.userDto.NewUserRegistrationResponse;
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

import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class RegisterNewUserService {

    private final AppUserRepositoryService repositoryService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserMappers mapper;
    private final AppUserRoleService roleService;
    private final UserAvailabilityRepository availabilityRepository;

    @Transactional
    public NewUserRegistrationResponse registerNewUser(NewUserRegistrationRequest request) {

        String name = request.name();
        String email = normalizeEmail(request.email());
        String password = passwordEncoder.encode(request.password().trim());
        String avatarUrl = (request.avatarUrl() == null || request.avatarUrl().isBlank() )? "N/A" : request.avatarUrl().trim();
        AppUserRole role = roleService.getRoleByNameOrThrow(UserRoleEnum.ROLE_USER.name());
        UserAvailability availability = availabilityRepository.save(new UserAvailability(AvailabilityEnum.AVAILABLE));

        isUserExistByEmail(email);

        AppUser userForSave = getUserForSave(name, email, password, avatarUrl, availability, role);
        AppUser savedUser = repositoryService.save(userForSave);

        return mapper.toNewUserResponse(savedUser);
    }

    private AppUser getUserForSave(String name, String email, String password, String avatarUrl, UserAvailability availability, AppUserRole role) {
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
}
