package org.lets_play_be.common;

public enum ErrorMessage {
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    MESSAGE_NOT_SENT("Message not sent");

    private final String message;

    ErrorMessage( final String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
