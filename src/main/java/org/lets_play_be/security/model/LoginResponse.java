package org.lets_play_be.security.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(@Schema(description = "jwt token expiration time", example = "2025-03-01T15:25:00+01:00")
        String accessTokenExpiration) {
}
