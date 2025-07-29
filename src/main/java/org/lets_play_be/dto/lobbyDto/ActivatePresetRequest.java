package org.lets_play_be.dto.lobbyDto;

import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

public record ActivatePresetRequest(
        @NotEmpty
        String message) implements Serializable {
}
