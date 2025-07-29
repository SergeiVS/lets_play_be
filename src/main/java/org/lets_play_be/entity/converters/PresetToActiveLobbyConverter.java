package org.lets_play_be.entity.converters;

import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.lobby.LobbyPreset;

public class PresetToActiveLobbyConverter {

    public static LobbyActive convertPresetToLobby(LobbyPreset preset) {
        return new LobbyActive(preset.getTitle(), preset.getTime(), preset.getOwner());
    }
}
