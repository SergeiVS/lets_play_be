package org.lets_play_be.common;

public enum ValidationMessages {
    EMAIL_FORMAT_ERROR("Email should be in proper format"),
    PASSWORD_FORMAT_ERROR("Password should be in proper format"),
    NOT_EMPTY_ERROR("Field should not be empty"),
    ;

    public final String message;

    ValidationMessages(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

}
