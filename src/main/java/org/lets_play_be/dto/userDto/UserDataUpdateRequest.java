package org.lets_play_be.dto.userDto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.NumberFormat;

import java.io.Serializable;

public record UserDataUpdateRequest(@NotNull(message = "UserId could not be null")
                                    @NumberFormat(style = NumberFormat.Style.NUMBER)
                                    Long userId,
                                    String newName,
                                    String newAvatarUrl)implements Serializable {
}
