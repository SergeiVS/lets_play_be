package org.lets_play_be.utils;

import org.lets_play_be.common.ValidationRegex;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class FormattingUtils {

    public static String normalizeEmail(String email) {

        if (email == null || email.isEmpty() || email.matches("\\s*")) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return email.replaceAll("\\s", "").trim().toLowerCase();
    }

    public static String timeToStringFormatter(OffsetTime localTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;

        if (localTime == null) {
            throw new IllegalArgumentException("Time is null");
        }

        return formatter.format(localTime);
    }

    public static String dateTimeToStringFormatter(OffsetDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (dateTime == null) {
            throw new IllegalArgumentException("Time is null");
        }
        return formatter.format(dateTime);
    }

    public static OffsetTime timeStringToOffsetTime(String timeString) {
     
        if (timeString == null || timeString.isEmpty()|| timeString.matches("\\s*")) {
            throw new IllegalArgumentException("timeString is null or empty");
        }
        if (!timeString.matches(ValidationRegex.ZEIT_FORMAT_REGEX.getRegex())) {
            throw new IllegalArgumentException("timeString is in invalid format");
        }

        return OffsetTime.parse(timeString);
    }
}
