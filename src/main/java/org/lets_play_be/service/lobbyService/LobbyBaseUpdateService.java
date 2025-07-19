package org.lets_play_be.service.lobbyService;

import lombok.RequiredArgsConstructor;
import org.lets_play_be.dto.lobbyDto.UpdateLobbyTitleAndTimeRequest;
import org.lets_play_be.entity.lobby.LobbyBase;
import org.springframework.stereotype.Service;

import java.time.OffsetTime;
import java.util.Objects;

import static org.lets_play_be.utils.FormattingUtils.timeStringToOffsetTime;


@Service
@RequiredArgsConstructor
public class LobbyBaseUpdateService {
  
        public void setNewValues(UpdateLobbyTitleAndTimeRequest request, LobbyBase lobbyForChange, long ownerId) {

        isLobbyOwner(lobbyForChange, ownerId);

        OffsetTime newTime = timeStringToOffsetTime(request.newTime());

        if (!request.newTitle().equals(lobbyForChange.getTitle())) {
            lobbyForChange.setTitle(request.newTitle());
        }
        if (lobbyForChange.getTime() != newTime) {
            lobbyForChange.setTime(newTime);
        }

    }

    public void isLobbyOwner(LobbyBase lobby, Long ownerId) {
        if (!Objects.equals(lobby.getOwner().getId(), ownerId)) {
            throw new IllegalArgumentException("User with Id: " + ownerId + " is not owner of this lobby.");
        }
    }
}
