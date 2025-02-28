package org.lets_play_be.utils;

public class FormattingUtils {

    private FormattingUtils() {throw new IllegalStateException("Utility class");}

    public static String normalizeEmail(String email) {
        return email.replaceAll("\\s", "").trim();
    }
}
