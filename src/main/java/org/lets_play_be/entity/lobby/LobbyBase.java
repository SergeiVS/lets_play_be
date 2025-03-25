package org.lets_play_be.entity.lobby;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;

@Getter
@ToString
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LobbyBase {
    private String title;
    @Setter(AccessLevel.PROTECTED)
    private LobbyType type;
    private OffsetTime time;
    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    public LobbyBase(String title, OffsetTime time, AppUser owner) {
        this.title = title;
        this.time = time;
        this.owner = owner;
    }
}
