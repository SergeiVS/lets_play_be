package org.lets_play_be.utils;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class FormattingUtils {

    public static String normalizeEmail(String email) {
        return email.replaceAll("\\s", "").trim();
    }

    private FormattingUtils() {throw new IllegalStateException("Utility class");}

    public static String timeToStringFormatter(OffsetTime localTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_TIME;
        if (localTime == null) {return null;}
        return formatter.format(localTime);
    }

    public static String dateTimeToStringFormatter(OffsetDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (dateTime == null) {return null;}
        return formatter.format(dateTime);
    }

    public static OffsetTime timeStringToOffsetTime(String timeString) {

        if (timeString == null||  timeString.isEmpty())return null;

        return OffsetTime.parse(timeString);
    }

    public static DateTimeFormatter getTimeFormatter(){
        return DateTimeFormatter.ofPattern("HHmmssZ");
    }
}
