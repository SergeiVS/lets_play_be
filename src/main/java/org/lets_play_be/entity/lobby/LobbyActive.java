package org.lets_play_be.entity.lobby;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.Invite.Invite;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor
public class LobbyActive extends LobbyBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "lobby", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Invite> invites;

    public LobbyActive(String title, OffsetTime time, AppUser owner) {
        super(title, time, owner);
        setType(LobbyType.ACTIVE);
        this.invites = new ArrayList<>();
    }
    public LobbyActive(long id, String title, OffsetTime time, AppUser owner) {
        super(title, time, owner);
        setType(LobbyType.ACTIVE);
        this.id = id;
        this.invites = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        LobbyActive that = (LobbyActive) o;
        return Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTitle());
    }
}
