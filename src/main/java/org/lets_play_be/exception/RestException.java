package org.lets_play_be.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public class RestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;

    public RestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
