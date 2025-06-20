package org.lets_play_be.entity.enums;

import java.util.Arrays;
import java.util.List;

public enum AvailabilityEnum {
    AVAILABLE,
    UNAVAILABLE,
    TEMPORARILY_UNAVAILABLE;

    public static List<String> getAvailabilityStrings() {
        return Arrays.stream(AvailabilityEnum.values())
                .map(AvailabilityEnum::toString).toList();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
