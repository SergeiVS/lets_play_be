package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.exception.RestException;
import org.lets_play_be.repository.LobbyPresetRepository;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class LobbyPresetService {

    private final LobbyPresetRepository repository;
    private final LobbyBaseUpdateService baseUpdateService;
    private final AppUserService appUserService;

    @Transactional
    public LobbyPresetFullResponse createNewLobbyPreset(NewLobbyRequest request, Authentication authentication) {

        LobbyPreset savedLobby = saveNewLobbyPreset(request, authentication);

        return new LobbyPresetFullResponse(savedLobby);
    }

    public List<LobbyPresetFullResponse> getAllUserPresets(Authentication authentication) {
        AppUser owner = getOwner(authentication);
        List<LobbyPreset> lobbies = repository.findByOwnerId(owner.getId());
        return getListOfPresetsFullResponse(lobbies);
    }

    @Transactional
    public LobbyPresetFullResponse addNewUsersToLobbyPreset(ChangeLobbyPresetUsersRequest request) {

        LobbyPreset presetForChange = getLobbyByIdOrThrow(request.lobbyId());

        List<AppUser> usersForAdd = getUsersForAdd(presetForChange, request.usersId());

        presetForChange.getUsers().addAll(usersForAdd);

        LobbyPreset savedPreset = repository.save(presetForChange);

        return new LobbyPresetFullResponse(savedPreset);
    }

    @Transactional
    public LobbyPresetFullResponse removeUserFromPreset(ChangeLobbyPresetUsersRequest request) {

        LobbyPreset presetForRemove = getLobbyByIdOrThrow(request.lobbyId());

        removeUsersFromPreset(request, presetForRemove);

        LobbyPreset savedPreset = repository.save(presetForRemove);

        return new LobbyPresetFullResponse(savedPreset);
    }

    @Transactional
    public StandardStringResponse removeLobbyPreset(Long id) {

        repository.deleteById(id);

        return new StandardStringResponse("Lobby preset with id: " + id + " is deleted");
    }


    @Transactional
    public LobbyPresetFullResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request, Authentication auth) {

        var owner = appUserService.getUserIdByEmailOrThrow(auth.getName());

        var presetForChange = getLobbyByIdOrThrow(request.id());

        isLobbyOwner(presetForChange, owner);

        OffsetTime newTime = FormattingUtils.timeStringToOffsetTime(request.newTime());

        baseUpdateService.setNewValues(request, presetForChange, newTime);

        var savedPreset = repository.save(presetForChange);

        return new LobbyPresetFullResponse(savedPreset);
    }

    public LobbyPreset getLobbyByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    private List<LobbyPresetFullResponse> getListOfPresetsFullResponse(List<LobbyPreset> presets) {
        return presets
                .stream()
                .map(LobbyPresetFullResponse::new)
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
        return repository.save(lobbyToSave);
    }

    private AppUser getOwner(Authentication authentication) {
        String email = authentication.getName();
        return appUserService.getUserByEmailOrThrow(email);
    }

    private void isLobbyOwner(LobbyPreset lobby, long ownerId){
        if(lobby.getOwner().getId() != ownerId){
            throw new RestException("User is not lobby owner", HttpStatus.BAD_REQUEST);
        }
    }

}
