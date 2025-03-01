package org.lets_play_be.dto.userDto;

public record NewUserRegistrationResponse(
        Long userId,
        String name,
        String email,
        String avatarUrl,
        String availability

) {
}
