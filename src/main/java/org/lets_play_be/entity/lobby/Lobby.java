package org.lets_play_be.entity.lobby;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.invite.Invite;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor
public class Lobby extends LobbyBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "lobby", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Invite> invites;

    public Lobby(String title, OffsetTime time, AppUser owner) {
        super(title, time, owner);
        setType(LobbyType.INACTIVE);
        this.invites = new ArrayList<>();
    }

    public Lobby(long id, String title, OffsetTime time, AppUser owner) {
        super(title, time, owner);
        setType(LobbyType.INACTIVE);
        this.id = id;
        this.invites = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lobby that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
