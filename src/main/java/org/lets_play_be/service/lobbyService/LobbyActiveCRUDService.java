package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.notification.Invite;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyActiveCRUDService {

    private final LobbyActiveRepoService repoService;
    private final AppUserService userService;
    private final InviteService inviteService;
    private final LobbyMappers lobbyMappers;

    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication authentication) {

        AppUser owner = userService.getUserByEmailOrThrow(authentication.getName());

        isLobbyExistingByOwnerId(owner);

        var savedLobby = saveNewLobbyFromRequest(request, owner);

        return lobbyMappers.toActiveResponse(savedLobby);

    }


    //    TODO add method body
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {
        return null;
    }


    private LobbyActive saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {

        var title = request.title();

        OffsetTime time = timeStringToOffsetTime(request.time());

        LobbyActive lobbyForSave = new LobbyActive(title, time, owner);
        List<Invite> invitesForAdd = getSavedInviteList(request, lobbyForSave);

        lobbyForSave.getInvites().addAll(invitesForAdd);

        return repoService.save(lobbyForSave);
    }

    private List<Invite> getSavedInviteList(NewActiveLobbyRequest request, LobbyActive lobbyForSave) {

        List<AppUser> users = userService.getUsersListByIds(request.userIds());
        return inviteService.getListOfNewInvites(users, lobbyForSave, request.message());
    }

    private void isLobbyExistingByOwnerId(AppUser owner) {
        if (repoService.existByOwner(owner)) {
            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
    }
}
