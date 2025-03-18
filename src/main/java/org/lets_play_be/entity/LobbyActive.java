package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;

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
//    @OneToOne
//    @JoinColumn(nullable = false)
//    private AppUser owner;
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
