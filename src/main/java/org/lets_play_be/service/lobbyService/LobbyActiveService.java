package org.lets_play_be.service.lobbyService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.ActiveLobbyResponse;
import org.lets_play_be.dto.lobbyDto.NewActiveLobbyRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeResponse;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.user.AppUser;
import org.lets_play_be.repository.LobbyActiveRepository;
import org.lets_play_be.service.InviteService.InviteService;
import org.lets_play_be.service.appUserService.AppUserService;
import org.lets_play_be.service.mappers.LobbyMappers;
import org.lets_play_be.utils.FormattingUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.List;

import static org.lets_play_be.service.lobbyService.LobbyBaseUpdateService.setNewValues;
import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;

@Service
@RequiredArgsConstructor
public class LobbyActiveService {

    private final LobbyActiveRepository repository;
    private final AppUserService userService;
    private final InviteService inviteService;
    private final LobbyMappers lobbyMappers;


    @Transactional
    public ActiveLobbyResponse createActiveLobby(NewActiveLobbyRequest request, Authentication authentication) {

        var owner = userService.getUserByEmailOrThrow(authentication.getName());

        isLobbyExistingByOwnerId(owner);

        var savedLobby = saveNewLobbyFromRequest(request, owner);

        return lobbyMappers.toActiveResponse(savedLobby);
    }

    @Transactional
    public UpdateLobbyTitleAndTimeResponse updateLobbyTitleAndTime(UpdateLobbyTitleAndTimeRequest request) {

        var lobbyForChange = getLobbyByIdOrThrow(request.id());

        OffsetTime newTime = FormattingUtils.timeStringToOffsetTime(request.newTime());

        setNewValues(request, lobbyForChange, newTime);

        var savedLobby = repository.save(lobbyForChange);

        return lobbyMappers.toUpdateResponse(savedLobby, savedLobby.getId());
    }



    public LobbyActive getLobbyByIdOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No Lobby found with id: " + id));
    }


    private LobbyActive saveNewLobbyFromRequest(NewActiveLobbyRequest request, AppUser owner) {

        var title = request.title();

        OffsetTime time = timeStringToOffsetTime(request.time());

        LobbyActive lobbyForSave = new LobbyActive(title, time, owner);
        List<Invite> invitesForAdd = getSavedInviteList(request, lobbyForSave);

        lobbyForSave.getInvites().addAll(invitesForAdd);

        return repository.save(lobbyForSave);
    }

    private List<Invite> getSavedInviteList(NewActiveLobbyRequest request, LobbyActive lobbyForSave) {

        List<AppUser> users = userService.getUsersListByIds(request.userIds());
        return inviteService.getListOfNewInvites(users, lobbyForSave, request.message());
    }

    private void isLobbyExistingByOwnerId(AppUser owner) {
        if (repository.existsById(owner.getId())) {
            throw new IllegalArgumentException("The Lobby for given owner already exists");
        }
    }
}
