package org.lets_play_be.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.NonNull;

public record LoginRequest(

        @NonNull
        @Pattern(regexp = "^[a-zA-Z0-9](?!.*\\.\\.)(?:[a-zA-Z0-9._%+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
                message = "Email must be a valid email address")
        @Schema(description = "email? used as username", example = "JohnB@testemail.com")
        String email,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password is given in wrong format")
        @Schema(description = "user password", example = "User@Test1")
        String password
) {
}
