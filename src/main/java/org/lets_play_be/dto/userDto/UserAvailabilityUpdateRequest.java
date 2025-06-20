package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record UserAvailabilityUpdateRequest(@NotEmpty(message = "Field availability should be filled")
                                            String newAvailability,
                                            @NotNull(message = "Field could not be  null")
                                            @Pattern(regexp = "(?:2[0-3]|[01]\\d|\\d)}:[0-5]{1}\\d{1}:[0-5]{1}\\d{1}[+|-]?0\\d|1[0-4]:[0-5][0-9]",
                                                    message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                            String newFromUnavailable,
                                            @NotNull(message = "Field could not be  null")
                                            @Pattern(regexp = "(?:2[0-3]|[01]\\d|\\d)}:[0-5]{1}\\d{1}:[0-5]{1}\\d{1}[+|-]?0\\d|1[0-4]:[0-5][0-9]",
                                                    message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                            String newToUnavailable
) implements Serializable {
}
