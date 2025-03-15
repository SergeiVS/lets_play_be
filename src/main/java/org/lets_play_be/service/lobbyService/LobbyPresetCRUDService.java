package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ChangeLobbyPresetUsersRequest;
import org.lets_play_be.dto.lobbyDto.LobbyPresetFullResponse;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.LobbyPreset;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LobbyPresetCRUDService {

    private final LobbyPresetRepoService repoService;
    private final AppUserService appUserService;
    private final LobbyMappers lobbyMappers;

    @Transactional
    public LobbyPresetFullResponse createNewLobbyPreset(NewLobbyPresetRequest request, Authentication authentication) {

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

//    TODO add method logic
    @Transactional
    public LobbyPresetFullResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication authentication) {
        return null;
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
        return getUsers(usersId);
    }

    private LobbyPreset saveNewLobbyPreset(NewLobbyPresetRequest request, Authentication authentication) {

        AppUser owner = getOwner(authentication);
        OffsetTime time = FormattingUtils.TIME_STRING_TO_OFFSET_TIME(request.time());
        List<AppUser> users = getUsers(request.userIds());

        LobbyPreset lobbyToSave = new LobbyPreset(request.title(), time, owner, users);
        return repoService.save(lobbyToSave);
    }

    private List<AppUser> getUsers(List<Long> userIds) {
        return userIds.stream().map(appUserService::getUserByIdOrThrow).toList();
    }

    private AppUser getOwner(Authentication authentication) {
        String email = authentication.getName();
        return appUserService.getUserByEmailOrThrow(email);
    }

}
