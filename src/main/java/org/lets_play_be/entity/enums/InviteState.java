package org.lets_play_be.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum InviteState {
    PENDING,
    ACCEPTED,
    DECLINED,
    DELAYED;

    @Override
    public String toString() {
        return  super.toString();
    }

    public List<String> getValuesSStringsList(){
        return Arrays.stream(InviteState.values()).map(InviteState::toString).toList();
    }
}
