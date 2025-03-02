package org.lets_play_be.utils;

import org.lets_play_be.entity.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;

import java.time.LocalTime;
import java.time.OffsetTime;

import static org.lets_play_be.entity.enums.AvailabilityEnum.getAvailabilityStrings;

public class ValidationUtils {

    public static void validateAvailabilityString(String newAvailability) {
        if (!getAvailabilityStrings().contains(newAvailability.toUpperCase())) {
            throw new IllegalArgumentException("The new Availability do not meet the AvailabilityEnum");
        }
    }

    public static void validateTimeOptionByTemp_Av(UserAvailability availability, OffsetTime fromAvailable, OffsetTime toAvailable) {
        if (availability.getAvailabilityType().equals(AvailabilityEnum.TEMPORARILY_AVAILABLE)) {
            String errorMessage = "In a case of TEMPORARY_AVAILABLE from and to time fields must be filled";
            assert fromAvailable != null : errorMessage;
            assert toAvailable != null : errorMessage;
        }
        isFromTimeBeforeTo(fromAvailable, toAvailable);

    }

    public static void isFromTimeBeforeTo(OffsetTime fromAvailable, OffsetTime toAvailable) {
        if(toAvailable.isBefore(fromAvailable)) {
            throw new IllegalArgumentException("To time field must be after from");
        }
    }
}
