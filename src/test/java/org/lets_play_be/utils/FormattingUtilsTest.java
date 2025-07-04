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

}