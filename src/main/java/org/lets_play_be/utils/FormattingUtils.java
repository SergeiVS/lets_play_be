package org.lets_play_be.utils;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

public class FormattingUtils {

    public static String normalizeEmail(String email) {
        return email.replaceAll("\\s", "").trim();
    }

    private FormattingUtils() {throw new IllegalStateException("Utility class");}

    public static String timeToStringFormatter(OffsetTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;
        if (localTime == null) {return null;}
        return formatter.format(localTime);
    };

    public static OffsetTime timeStringToOffsetTime(String timeString) {

        if (timeString == null||  timeString.isEmpty())return null;

        assertTrue(timeString.matches("[0-2]{1}\\d{1}:[0-6]{1}\\d{1}:[0-6]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]"), timeString);

        return OffsetTime.parse(timeString);
    }

    public static DateTimeFormatter getTimeFormatter(){
        return DateTimeFormatter.ofPattern("HHmmssZ");
    }
}
