package org.lets_play_be.exception;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private List<Violation> errors = new ArrayList<>();
}
