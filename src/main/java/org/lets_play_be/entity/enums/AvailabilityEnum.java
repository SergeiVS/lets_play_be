package org.lets_play_be.entity.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AvailabilityEnum {
    AVAILABLE,
    UNAVAILABLE,
    TEMPORARILY_AVAILABLE;

    public static List<AvailabilityEnum> getAvailable() {
        return Arrays.stream(AvailabilityEnum.values()).toList();
    }

    public static List<String> getAvailabilityStrings() {
        return Arrays.stream(AvailabilityEnum.values())
                .map(AvailabilityEnum::toString).toList();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
