package org.lets_play_be.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record LoginRequest(

        @NotBlank(message = "Email cannot be empty")
        @Pattern(regexp = "^[a-zA-Z0-9](?!.*\\.\\.)(?:[a-zA-Z0-9._%+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
                message = "Field email must be a valid email address")
        @Schema(description = "email? used as username", example = "JohnB@testemail.com")
        String email,
        @NotBlank(message = "Password cannot be empty")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password is given in wrong format")
        @Schema(description = "user password", example = "User@Test1")
        String password
) implements Serializable {
}
