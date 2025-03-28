package org.lets_play_be.service.appUserService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.entity.user.AppUser;
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

    public List<AppUser> getUsersByIds(List<Long> ids) {
        return appUserRepository.getAppUsersById(ids);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findAppUserByEmail(email);
    }

    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    public boolean existsByName(String name) {
        return appUserRepository.existsByName(name);
    }
}
