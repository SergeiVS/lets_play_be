package org.lets_play_be.entity.lobby;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;


@Getter
@ToString
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LobbyBase {

    @Setter
    private String title;

    @Setter
    @Enumerated(EnumType.STRING)
    private LobbyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id", unique = true)
    private AppUser owner;

    @Setter
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime time;

    public LobbyBase(String title, OffsetTime time, AppUser owner) {
        this.title = title;
        this.time = time;
        this.owner = owner;
    }
}
