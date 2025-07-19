package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record NewUserRegistrationRequest(
        @NotNull(message = "Name should not be null")
        @Size(min = 3, message = "Name should not be shorter than 3 symbol")
        String name,
        @NotEmpty(message = "Email should not be empty")
        @Pattern(regexp = "^[a-zA-Z0-9](?!.*\\.\\.)(?:[a-zA-Z0-9._%+-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$"
                , message = "Email should be in proper format")
//        @Email(message = "Email should be in proper format")
        String email,
        @NotEmpty(message = "Password should not be empty")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                message = "Password is given in wrong format")
        String password,
        String avatarUrl) implements Serializable {
}

