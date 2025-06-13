package org.lets_play_be.service.appUserService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.lets_play_be.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;

@Service
@RequiredArgsConstructor
public class GetUserProfileService {

    private final AppUserRepository appUserRepository;

    public AppUserProfile getUserProfile(String email) {

        return appUserRepository.findAppUserByEmail(normalizeEmail(email))
                .map(AppUserProfile::from)
                .orElseThrow(() -> new UsernameNotFoundException("The user with email: " + email + " not found"));
    }
}
