package org.lets_play_be.utils;

import org.lets_play_be.entity.user.UserAvailability;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.lets_play_be.entity.enums.LobbyType;

import java.time.OffsetTime;
import java.util.Arrays;

import static org.lets_play_be.entity.enums.AvailabilityEnum.getAvailabilityStrings;

public class ValidationUtils {

    public static void validateAvailabilityString(String newAvailability) {
        if (!getAvailabilityStrings().contains(newAvailability.toUpperCase())) {
            throw new IllegalArgumentException("The new Availability do not meet the AvailabilityEnum");
        }
    }

    public static void validateLobbyTypeString(String lobbyType) {
        if(!LobbyType.LobbyTypeStringList().contains(lobbyType.toUpperCase())) {
            throw new IllegalArgumentException("The LobbyType do not meet the LobbyTypeEnum");
        }
    }

    public static void validateTimeOptionByTemp_Av(UserAvailability availability, OffsetTime fromUnavailable, OffsetTime toUnavailable) {
        if (availability.getAvailabilityType().equals(AvailabilityEnum.TEMPORARILY_AVAILABLE)) {
            String errorMessage = "In a case of TEMPORARY_AVAILABLE from and to time fields must be filled";
            assert fromUnavailable != null : errorMessage;
            assert toUnavailable != null : errorMessage;

            isFromTimeBeforeTo(fromUnavailable, toUnavailable);
        }
    }

    public static void isFromTimeBeforeTo(OffsetTime fromUnavailable, OffsetTime toUnavailable) {
        if(toUnavailable.isBefore(fromUnavailable)) {
            throw new IllegalArgumentException("To time field must be after from");
        }
    }
}
