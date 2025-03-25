package org.lets_play_be.entity.lobby;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.lobby.LobbyBase;
import org.lets_play_be.entity.lobby.LobbyPreset;
import org.lets_play_be.entity.notification.Invite;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LobbyActive extends LobbyBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "lobby", orphanRemoval = true, cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    private List<Invite> invites;

    public LobbyActive(String title, OffsetTime time, AppUser owner) {
        super(title, time, owner);
        setType(LobbyType.ACTIVE);
        this.invites = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        LobbyActive that = (LobbyActive) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
