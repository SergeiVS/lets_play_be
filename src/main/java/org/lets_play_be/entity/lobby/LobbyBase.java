package org.lets_play_be.entity.lobby;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;


@Getter
@ToString
@MappedSuperclass
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class LobbyBase {
    @NaturalId
    private String title;

    @Setter(AccessLevel.PROTECTED)
    private LobbyType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id")
    private AppUser owner;

    private OffsetTime time;


    public LobbyBase(String title, OffsetTime time, AppUser owner) {
        this.title = title;
        this.time = time;
        this.owner = owner;
    }


}
