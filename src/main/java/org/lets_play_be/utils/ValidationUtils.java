package org.lets_play_be.utils;

import java.time.OffsetTime;

import static org.lets_play_be.entity.enums.AvailabilityEnum.getAvailabilityStrings;

public class ValidationUtils {

    public static void validateAvailabilityString(String newAvailability) {
        if (!getAvailabilityStrings().contains(newAvailability.toUpperCase())) {
            throw new IllegalArgumentException("The new Availability do not meet the AvailabilityEnum");
        }
    }

    public static void isFromTimeBeforeTo(OffsetTime fromUnavailable, OffsetTime toUnavailable) {
        if(toUnavailable.isBefore(fromUnavailable)) {
            throw new IllegalArgumentException("To time must be after from");
        }
    }
}
