package org.lets_play_be.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum AvailabilityEnum {
    AVAILABLE,
    UNAVAILABLE,
    TEMPORARILY_AVAILABLE;

    public static List<String> getAvailabilityStrings() {
        return Arrays.stream(AvailabilityEnum.values()).map(AvailabilityEnum::toString).toList();
    }

    public static List<AvailabilityEnum> getAvailabilityEnums() {
        return Arrays.asList(AvailabilityEnum.values());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
