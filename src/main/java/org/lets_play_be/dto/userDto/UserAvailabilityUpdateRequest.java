package org.lets_play_be.dto.userDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.NumberFormat;

public record UserAvailabilityUpdateRequest(@NotNull(message = "Field userId should be filled")
                                            @NumberFormat
                                            @Schema(example = "UNAVAILABLE")
                                            Long userId,
                                            @NotEmpty(message = "Field availability should be filled")
                                            String newAvailability,
                                            @Pattern(regexp = "[0-2]{1}\\d{1}:[0-6]{1}\\d{1}:[0-6]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                                    message = "Wrong time format. Expected: HH:MM+/-TZ")
                                            String newFromAvailable,
                                            @Pattern(regexp = "[0-2]{1}\\d{1}:[0-6]{1}\\d{1}:[0-6]{1}\\d{1}[+|-][0-1][0-9]:[0-5][0-9]",
                                                    message = "Wrong time format. Expected: HH:MM+/-TZ")
                                            String newToAvailable
) {
}
