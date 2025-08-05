package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.ChangePresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.NewPresetRequest;
import org.lets_play_be.dto.lobbyDto.PresetFullResponse;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;


@Service
@RequiredArgsConstructor
public class LobbyPresetService {

    private final LobbyPresetRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final AppUserService appUserService;

    @Transactional
    public PresetFullResponse createNewLobbyPreset(NewPresetRequest request, Authentication auth) {
        LobbyPreset savedLobby = saveNewLobbyPreset(request, auth);

        return new PresetFullResponse(savedLobby);
    }

    @Deprecated
    public List<PresetFullResponse> getAllUserPresets(Authentication auth) {
        AppUser owner = appUserService.getUserByEmailOrThrow(auth.getName());
        List<LobbyPreset> lobbies = repository.findByOwnerId(owner.getId());

        return getListOfPresetsFullResponse(lobbies);
    }

    public PresetFullResponse getUsersLobbyPreset(long userId, Authentication auth) {
        var owner = appUserService.getUserByEmailOrThrow(auth.getName());

        isPresetIdValid(Objects.equals(userId, owner.getId()));

        return getPresetFullResponse(owner);
    }

    public PresetFullResponse getPresetFullResponse(AppUser owner) {
        var preset = repository.findUniqueByOwnerId(owner.getId());

        return preset.map(PresetFullResponse::new).orElseGet(() -> new PresetFullResponse(getSavedBlankPreset(owner)));
    }

    @Transactional
    public PresetFullResponse addNewUsersToLobbyPreset(ChangePresetUsersRequest request, Authentication auth) {
        var presetForChange = getPresetByIdOrThrow(request.lobbyId());
        var owner = appUserService.getUserByEmailOrThrow(auth.getName());

        isOwner(Objects.equals(owner, presetForChange.getOwner()));

        List<AppUser> usersForAdd = appUserService.getUsersListByIds(request.usersId());

        LobbyPreset savedPreset = saveNewUsersToPreset(presetForChange, usersForAdd);

        return new PresetFullResponse(savedPreset);
    }

    @Transactional
    public PresetFullResponse removeUserFromPreset(ChangePresetUsersRequest request, Authentication auth) {
        var presetForRemove = getPresetByIdOrThrow(request.lobbyId());
        var owner = appUserService.getUserByEmailOrThrow(auth.getName());

        isOwner(Objects.equals(owner, presetForRemove.getOwner()));
        removeUsersFromPreset(request, presetForRemove);

        var savedPreset = repository.save(presetForRemove);

        return new PresetFullResponse(savedPreset);
    }

    @Transactional
    public StandardStringResponse removeLobbyPreset(Long id, Authentication auth) {
        var owner = appUserService.getUserByEmailOrThrow(auth.getName());
        var preset = getPresetByIdOrThrow(id);

        isOwner(Objects.equals(owner, preset.getOwner()));
        isPresetIdValid(Objects.equals(preset.getId(), id));

        repository.deleteById(id);

        return new StandardStringResponse("Lobby preset with lobbyId: " + id + " is deleted");
    }

    @Transactional
    public PresetFullResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {
        AppUser owner = appUserService.getUserByEmailOrThrow(auth.getName());

        LobbyPreset presetForChange = getPresetByIdOrThrow(request.lobbyId());

        baseUpdateService.setNewValues(request, presetForChange, owner.getId());

        LobbyPreset savedPreset = repository.save(presetForChange);

        return new PresetFullResponse(savedPreset);
    }

    public LobbyPreset getPresetByOwnerIdOrThrow(long ownerId) {
        return repository.findUniqueByOwnerId(ownerId).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    private LobbyPreset getPresetByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    private void isOwner(boolean isOwner) {
        if (!isOwner) throw new IllegalArgumentException("Current User is not the same as the preset owner");
    }

    private void isPresetIdValid(boolean isValid) {
        if (!isValid) throw new IllegalArgumentException("Id don't match authenticated Users preset id");
    }

    private LobbyPreset getSavedBlankPreset(AppUser owner) {
        var blankPreset = new LobbyPreset(owner);
        return repository.save(blankPreset);
    }

    private List<PresetFullResponse> getListOfPresetsFullResponse(List<LobbyPreset> presets) {
        return presets
                .stream()
                .map(PresetFullResponse::new)
                .toList();
    }

    private LobbyPreset saveNewUsersToPreset(LobbyPreset presetForChange, List<AppUser> usersForAdd) {
        usersForAdd.forEach(appUser -> {
            if (!presetForChange.getUsers().contains(appUser)) {
                presetForChange.getUsers().add(appUser);
            }
        });

        return repository.save(presetForChange);
    }

    private void removeUsersFromPreset(ChangePresetUsersRequest request, LobbyPreset presetForRemove) {
        presetForRemove.getUsers().removeIf(user -> request.usersId().contains(user.getId()));
    }

    private LobbyPreset saveNewLobbyPreset(NewPresetRequest request, Authentication auth) {
        AppUser owner = appUserService.getUserByEmailOrThrow(auth.getName());

        OffsetTime time = timeStringToOffsetTime(request.time());

        List<AppUser> users = appUserService.getUsersListByIds(request.userIds());

        LobbyPreset lobbyToSave = new LobbyPreset(request.title(), time, owner, users);

        return repository.save(lobbyToSave);
    }
}
