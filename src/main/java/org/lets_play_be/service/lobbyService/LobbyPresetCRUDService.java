package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

import static org.lets_play_be.service.lobbyService.LobbyBaseUpdateService.setNewValues;

@Service
@RequiredArgsConstructor
public class LobbyPresetCRUDService {

    private final LobbyPresetRepoService repoService;
    private final AppUserService appUserService;
    private final LobbyMappers lobbyMappers;

    @Transactional
    public LobbyPresetFullResponse createNewLobbyPreset(NewLobbyRequest request, Authentication authentication) {

        LobbyPreset savedLobby = saveNewLobbyPreset(request, authentication);

        return lobbyMappers.toLobbyPresetFullResponse(savedLobby);
    }

    public List<LobbyPresetFullResponse> getAllUserPresets(Authentication authentication) {
        AppUser owner = getOwner(authentication);
        List<LobbyPreset> lobbies = repoService.findByOwnerId(owner.getId());
        return getListOfPresetsFullResponse(lobbies);
    }

    @Transactional
    public LobbyPresetFullResponse addNewUsersToLobbyPreset(ChangeLobbyPresetUsersRequest request) {

        LobbyPreset presetForChange = getLobbyByIdOrThrow(request.lobbyId());

        List<AppUser> usersForAdd = getUsersForAdd(presetForChange, request.usersId());

        presetForChange.getUsers().addAll(usersForAdd);

        LobbyPreset savedPreset = repoService.save(presetForChange);

        return lobbyMappers.toLobbyPresetFullResponse(savedPreset);
    }

    @Transactional
    public LobbyPresetFullResponse removeUserFromPreset(ChangeLobbyPresetUsersRequest request) {

        LobbyPreset presetForRemove = getLobbyByIdOrThrow(request.lobbyId());

        removeUsersFromPreset(request, presetForRemove);

        LobbyPreset savedPreset = repoService.save(presetForRemove);

        return lobbyMappers.toLobbyPresetFullResponse(savedPreset);
    }

    @Transactional
    public StandardStringResponse removeLobbyPreset(Long id) {
        repoService.deleteById(id);

        return new StandardStringResponse("Lobby preset with id: " + id + " is deleted");
    }


    @Transactional
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {

        var presetForChange = getLobbyByIdOrThrow(request.id());

        OffsetTime newTime = FormattingUtils.timeStringToOffsetTime(request.newTime());

        setNewValues(request, presetForChange, newTime);

        var savedPreset = repoService.save(presetForChange);

        return lobbyMappers.toUpdateResponse(savedPreset, savedPreset.getId());
    }

    public LobbyPreset getLobbyByIdOrThrow(Long id) {
        return repoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    private List<LobbyPresetFullResponse> getListOfPresetsFullResponse(List<LobbyPreset> presets) {
        return presets
                .stream()
                .map(lobbyMappers::toLobbyPresetFullResponse)
                .toList();
    }

    private static void removeUsersFromPreset(ChangeLobbyPresetUsersRequest request, LobbyPreset presetForRemove) {
        presetForRemove.getUsers().removeIf(user -> request.usersId().contains(user.getId()));
    }

    private List<AppUser> getUsersForAdd(LobbyPreset presetForChange, List<Long> usersId) {
        for (AppUser user : presetForChange.getUsers()) {
            usersId.remove(user.getId());
        }
        return appUserService.getUsersListByIds(usersId);
    }

    private LobbyPreset saveNewLobbyPreset(NewLobbyRequest request, Authentication authentication) {

        AppUser owner = getOwner(authentication);
        OffsetTime time = FormattingUtils.timeStringToOffsetTime(request.time());
        List<AppUser> users = appUserService.getUsersListByIds(request.userIds());

        LobbyPreset lobbyToSave = new LobbyPreset(request.title(), time, owner, users);
        return repoService.save(lobbyToSave);
    }

    private AppUser getOwner(Authentication authentication) {
        String email = authentication.getName();
        return appUserService.getUserByEmailOrThrow(email);
    }

}
