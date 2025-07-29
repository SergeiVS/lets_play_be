package org.lets_play_be.dto;

import jakarta.validation.constraints.NotEmpty;

public record StandardStringResponse(@NotEmpty String message) {
}
