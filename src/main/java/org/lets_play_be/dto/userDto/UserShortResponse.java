package org.lets_play_be.dto.userDto;

import java.io.Serializable;

public record UserShortResponse(
        Long id,
        String name
) implements Serializable {
}
