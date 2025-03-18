package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.StandardStringResponse;
import org.lets_play_be.dto.lobbyDto.*;
import org.lets_play_be.entity.AppUser;
import org.lets_play_be.entity.Invite;
import org.lets_play_be.entity.LobbyActive;
import org.lets_play_be.entity.LobbyPreset;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LobbyPresetCRUDService {

    private final LobbyPresetRepoService repoService;
    private final LobbyActiveRepoService activeRepoService;
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

    @Transactional
    public StandardStringResponse removeLobbyPreset(Long id) {
        Optional<LobbyPreset> presetDeletedOptional = repoService.deleteById(id);
        if (presetDeletedOptional.isEmpty()) {
            throw new IllegalArgumentException("Lobby with id: " + id + ", is not found");
        }
        return new StandardStringResponse("Lobby preset " + presetDeletedOptional.get().getTitle() + ", with id" + presetDeletedOptional.get().getId() + " is deleted");
    }


    @Transactional
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {
        LobbyPreset presetForChange = getLobbyByIdOrThrow(request.id());
        OffsetTime newTime = FormattingUtils.TIME_STRING_TO_OFFSET_TIME(request.newTime());
        setNewValues(request, presetForChange, newTime);
        LobbyPreset savedPreset = repoService.save(presetForChange);
        return lobbyMappers.toUpdateResponse(savedPreset);
    }

    @Transactional
    public ActiveLobbyResponse activateLobbyPreset(ActivateLobbyPresetRequest request) {
        LobbyPreset preset = getLobbyByIdOrThrow(request.presetId());
        AppUser owner = preset.getOwner();
        isActiveLobbyUniqueForOwner(owner);
        LobbyActive active = preset.activateLobby();
        List<Invite> invites = getInvites(request.userIds(), active, request.message());
        active.getInvites().addAll(invites);
        LobbyActive savedActive = activeRepoService.save(active);
        return lobbyMappers.toActiveResponse(savedActive);
    }

    private void isActiveLobbyUniqueForOwner(AppUser owner) {
        if(activeRepoService.existByOwner(owner)){
            throw new IllegalArgumentException("User: " + owner + " has already an active lobby");
        }
    }

    private List<Invite> getInvites(List<Long> userIds, LobbyActive active, String message) {
        List<AppUser> users = appUserService.getUsersListByIds(userIds);

        if (users.size() != userIds.size()) {
            throw new IllegalArgumentException("One or more users are not exist. Found: " + users.size() + " users.");
        }

        return users.stream().map(user -> new Invite(user, active, message)).toList();
    }


    private static void setNewValues(UpdateLobbyTitleAndTimeRequest request, LobbyPreset presetForChange, OffsetTime newTime) {
        if (!request.newTitle().equals(presetForChange.getTitle())) {
            presetForChange.setTitle(request.newTitle());
        }
        if (presetForChange.getTime() != newTime) {
            presetForChange.setTime(newTime);
        }
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
