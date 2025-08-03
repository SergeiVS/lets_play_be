package org.lets_play_be.common;

import lombok.Getter;


@Getter
public enum ValidationRegex {
    TIME_FORMAT_REGEX("([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d[+-](0\\d|1[0-4]):[0-5]\\d");
    final String regex;

    ValidationRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return this.regex;
    }
}
