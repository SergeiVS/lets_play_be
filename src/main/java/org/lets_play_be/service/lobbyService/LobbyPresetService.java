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

import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;


@Service
@RequiredArgsConstructor
public class LobbyPresetService {

    private final LobbyPresetRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final AppUserService appUserService;

    @Transactional
    public PresetFullResponse createNewLobbyPreset(NewPresetRequest request, Authentication authentication) {

        LobbyPreset savedLobby = saveNewLobbyPreset(request, authentication);

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

        if (owner.getId() != userId) throw new IllegalArgumentException("Id don't match authenticated User id");

        var preset = repository.findUniqueByOwnerId(owner.getId());

        if (preset.isEmpty()) {
            var savedBlankPreset = getSavedBlankPreset(owner);
            return new PresetFullResponse(savedBlankPreset);
        }

        return new PresetFullResponse(preset.get());
    }

    @Transactional
    public PresetFullResponse addNewUsersToLobbyPreset(ChangePresetUsersRequest request) {

        LobbyPreset presetForChange = getPresetByIdOrThrow(request.lobbyId());

        List<AppUser> usersForAdd = getUsersForAdd(presetForChange, request.usersId());

        presetForChange.getUsers().addAll(usersForAdd);

        LobbyPreset savedPreset = repository.save(presetForChange);

        return new PresetFullResponse(savedPreset);
    }

    @Transactional
    public PresetFullResponse removeUserFromPreset(ChangePresetUsersRequest request) {

        LobbyPreset presetForRemove = getPresetByIdOrThrow(request.lobbyId());

        removeUsersFromPreset(request, presetForRemove);

        LobbyPreset savedPreset = repository.save(presetForRemove);

        return new PresetFullResponse(savedPreset);
    }

    @Transactional
    public StandardStringResponse removeLobbyPreset(Long id) {

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

    public LobbyPreset getPresetByIdOrThrow(Long id) {

        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    public LobbyPreset getPresetByOwnerIdOrThrow(long ownerId) {

        return repository.findUniqueByOwnerId(ownerId).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
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

    private static void removeUsersFromPreset(ChangePresetUsersRequest request, LobbyPreset presetForRemove) {

        presetForRemove.getUsers().removeIf(user -> request.usersId().contains(user.getId()));
    }

    private List<AppUser> getUsersForAdd(LobbyPreset presetForChange, List<Long> usersId) {

        for (AppUser user : presetForChange.getUsers()) {
            usersId.remove(user.getId());
        }

        return appUserService.getUsersListByIds(usersId);
    }

    private LobbyPreset saveNewLobbyPreset(NewPresetRequest request, Authentication auth) {

        AppUser owner = appUserService.getUserByEmailOrThrow(auth.getName());

        OffsetTime time = timeStringToOffsetTime(request.time());

        List<AppUser> users = appUserService.getUsersListByIds(request.userIds());

        LobbyPreset lobbyToSave = new LobbyPreset(request.title(), time, owner, users);

        return repository.save(lobbyToSave);
    }
}
