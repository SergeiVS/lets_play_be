package org.lets_play_be.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.lets_play_be.utils.FormattingUtils.normalizeEmail;

class FormattingUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {" test @ Test. Com "})
    void normalizeEmail_Success(String email) {
        String normalizedEmail = "test@test.com";

        assertEquals(normalizedEmail, normalizeEmail(email));
    }

    @Test
    void timeToStringFormatter_Throws_TimeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> FormattingUtils.timeToStringFormatter(null));
    }

    @Test
    void dateTimeToStringFormatter_Throws_TimeDateIsNull() {
        assertThrows(IllegalArgumentException.class, () -> FormattingUtils.dateTimeToStringFormatter(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void normalizeEmail_Throws(String emptyString) {

        assertThrowsExactly(IllegalArgumentException.class, () -> normalizeEmail(emptyString), "Email cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"19:25:30+01:00", "19:25:30-01:00"})
    void timeStringToOffsetTime_Success(String timeString) {

        var result = FormattingUtils.timeStringToOffsetTime(timeString);

        assertEquals(timeString.substring(0, 2), String.valueOf(result.getHour()));
        assertEquals(timeString.substring(3, 5), String.valueOf(result.getMinute()));
        assertEquals(timeString.substring(6, 8), String.valueOf(result.getSecond()));
        assertEquals(timeString.substring(8), String.valueOf(result.getOffset()));

    }

    @ParameterizedTest
    @ValueSource(strings = {"24:00:30+01:00", "00:60:30+01:00", "00:59:60+01:00", "00:59:60+15:00", "", " "})
    void timeStringToOffsetTime_Trows_IllegalArgumentException_WrongFormat(String timeString) {

        assertThrows(IllegalArgumentException.class, () -> FormattingUtils.timeStringToOffsetTime(timeString));
    }

    @Test
    void timeStringToOffsetTime_Trows_IllegalArgumentException_EmptyOrNull() {

        assertThrows(IllegalArgumentException.class, () -> FormattingUtils.timeStringToOffsetTime(null));
    }
}