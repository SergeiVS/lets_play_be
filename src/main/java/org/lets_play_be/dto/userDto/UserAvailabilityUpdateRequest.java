package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

public record UserAvailabilityUpdateRequest(@NotEmpty(message = "Field availability should be filled")
                                            String newAvailability,
                                            @NotNull(message = "Field can not be  null")
                                            @Pattern(regexp = "([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d[+\\-](0\\d|1[0-4]):[0-5]\\d",
                                                    message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                            String newUnavailableFrom,
                                            @NotNull(message = "Field can not be  null")
                                            @Pattern(regexp = "([0-1]\\d|2[0-3]):[0-5]\\d:[0-5]\\d[+\\-](0\\d|1[0-4]):[0-5]\\d",
                                                    message = "Wrong time format. Expected: HH:MM+/-HH:MM")
                                            String newUnavailableTo
) implements Serializable {
}
