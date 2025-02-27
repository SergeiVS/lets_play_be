package org.lets_play_be.service.appUserService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.userDto.AppUserProfile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserProfileService  {

    private final AppUserRepositoryService repositoryService;

    public AppUserProfile getUserProfile(String email) {
        return repositoryService.findByEmail(email)
                .map(AppUserProfile::from)
                .orElseThrow(() -> new UsernameNotFoundException("The user with email: " + email + " not found"));
    }
}
