package org.lets_play_be.security.model;

import jakarta.validation.constraints.Pattern;
import lombok.NonNull;

public record LoginRequest(

        @NonNull
        @Pattern(regexp = "^[a-zA-Z0-9](?!.*\\.\\.)(?:[a-zA-Z0-9._%+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
                message = "Email must be a valid email address")
        String email,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
        message = "Password is given in wrong format")
        String password
) {
}
