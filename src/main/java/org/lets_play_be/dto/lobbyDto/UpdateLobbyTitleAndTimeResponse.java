package org.lets_play_be.dto.lobbyDto;

import org.lets_play_be.dto.userDto.UserShortResponse;

import java.io.Serializable;

public record UpdateLobbyTitleAndTimeResponse(Long id,
                                              String type,
                                              UserShortResponse owner,
                                              String title,
                                              String time) implements Serializable {
}
