package org.lets_play_be.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.lets_play_be.entity.enums.LobbyType;

import java.time.OffsetTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class LobbyBase {
    private String title;
    @Setter(AccessLevel.PROTECTED)
    private LobbyType type;
    private OffsetTime time;

    public LobbyBase(String title, OffsetTime time) {
        this.title = title;
        this.time = time;
    }
}
