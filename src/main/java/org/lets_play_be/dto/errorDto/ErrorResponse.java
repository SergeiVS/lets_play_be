package org.lets_play_be.dto.errorDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dto for represent exception to client")
public record ErrorResponse(String message) {
}
