package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record NewUserRegistrationRequest(
        @NotEmpty(message = "Field should not be empty")
        String name,
        @NotEmpty(message = "Field should not be empty")
        @Email(message = "Email should be in proper format")
        String email,
        @NotEmpty(message = "Field should not be empty")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password is given in wrong format")
        String password,
        String avatarUrl) implements Serializable {
}

