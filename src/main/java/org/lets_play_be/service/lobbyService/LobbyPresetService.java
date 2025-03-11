package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetRequest;
import org.lets_play_be.dto.lobbyDto.NewLobbyPresetResponse;
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
public class LobbyPresetService {

    private final LobbyPresetRepoService repoService;
    private final AppUserService appUserService;
    private final LobbyMappers lobbyMappers;

    public NewLobbyPresetResponse createNewLobbyPreset(NewLobbyPresetRequest request, Authentication authentication) {

        LobbyPreset savedLobby = saveNewLobbyPreset(request, authentication);

        return lobbyMappers.toNewLobbyPresetResponse(savedLobby);
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
