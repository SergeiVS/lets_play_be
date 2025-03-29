package org.lets_play_be.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum LobbyType {
    PRESET,
    ACTIVE;

    @Override
    public String toString() {
        return super.toString();
    }

    public static List<String> LobbyTypeStringList(){
        return Arrays.stream(LobbyType.values()).map(LobbyType::toString).toList();
    }
}
