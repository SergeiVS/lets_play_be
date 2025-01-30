package org.lets_play_be.security.utils;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.service.appUserService.AppUserRepositoryService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepositoryService repositoryService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AppUser> user = repositoryService.findByEmail(username);
        return user.map(UserDetailsMapper::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
