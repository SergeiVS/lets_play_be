package org.lets_play_be.security.utils;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AppUser> user = userRepository.findAppUserByEmail(username);
        return user.map(UserDetailsMapper::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
