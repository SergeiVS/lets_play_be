package org.lets_play_be.service.appUserService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserRepositoryService {

    private final AppUserRepository appUserRepository;

    public AppUser save(AppUser appUser) {
       return appUserRepository.save(appUser);
    }

    public List<AppUser> saveAll(List<AppUser> appUsers) {
        return appUserRepository.saveAll(appUsers);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findAppUserByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }
}
