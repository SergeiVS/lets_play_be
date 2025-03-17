package org.lets_play_be.dto.lobbyDto;

import org.lets_play_be.dto.userDto.InvitedUserResponse;
import org.lets_play_be.dto.userDto.UserShortResponse;

import java.io.Serializable;
import java.util.List;

public record ActiveLobbyResponse(long id,
                                  String date,
                                  UserShortResponse owner,
                                  String lobbyType,
                                  String title,
                                  List<InvitedUserResponse> invitedUsers) implements Serializable {
}
