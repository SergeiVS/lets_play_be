package org.lets_play_be.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;

class FormattingUtilsTest {

    @Test
    void normalizeEmail_Success() {
        String notNormalizedEmail = " test @ Test. Com ";
        String normalizedEmail = "test@test.com";

        assertEquals(normalizedEmail, normalizeEmail(notNormalizedEmail));
        assertEquals(normalizedEmail, normalizeEmail(normalizedEmail));
    }

    @Test
    void normalizeEmail_Throws() {

        assertThrowsExactly(IllegalArgumentException.class, () -> normalizeEmail(""), "Email cannot be null or empty");
        assertThrowsExactly(IllegalArgumentException.class, () -> normalizeEmail(" "), "Email cannot be null or empty");
    }

    @Test
    void timeStringToOffsetTime() {

        String correctTimeString1 = "19:25:30+01:00";
        String correctTimeString2 = "19:25:30-01:00";

        String incorrectTimeStringHours = "24:00:30+01:00";
        String incorrectTimeStringMinutes = "00:60:30+01:00";
        String incorrectTimeStringSeconds = "00:59:60+01:00";

        var result1 = FormattingUtils.timeStringToOffsetTime(correctTimeString1);
        var result2 = FormattingUtils.timeStringToOffsetTime(correctTimeString2);

        assertEquals(correctTimeString1.substring(0, 2), String.valueOf(result1.getHour()));
        assertEquals(correctTimeString1.substring(3, 5), String.valueOf(result1.getMinute()));
        assertEquals(correctTimeString1.substring(6, 8), String.valueOf(result1.getSecond()));
        assertEquals(correctTimeString1.substring(8), String.valueOf(result1.getOffset()));

        assertEquals(correctTimeString2.substring(0, 2), String.valueOf(result2.getHour()));
        assertEquals(correctTimeString2.substring(3, 5), String.valueOf(result2.getMinute()));
        assertEquals(correctTimeString2.substring(6, 8), String.valueOf(result2.getSecond()));
        assertEquals(correctTimeString2.substring(8), String.valueOf(result2.getOffset()));

        assertThrows(AssertionError.class, () -> FormattingUtils.timeStringToOffsetTime(incorrectTimeStringHours));
        assertThrows(AssertionError.class, () -> FormattingUtils.timeStringToOffsetTime(incorrectTimeStringMinutes));
        assertThrows(AssertionError.class, () -> FormattingUtils.timeStringToOffsetTime(incorrectTimeStringSeconds));
    }
}