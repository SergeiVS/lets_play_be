package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.Nullable;

public record UserAvailabilityUpdateRequest(@NotEmpty(message = "Field userId should be filled")
                                            Long userId,
                                            @NotEmpty(message = "Field availability should be filled")
                                            String newAvailability,
                                            @Nullable
                                            String newFromAvailable,
                                            @Nullable
                                            String newToAvailable
) {
}
