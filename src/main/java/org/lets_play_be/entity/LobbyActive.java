package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Enumerated(EnumType.STRING)
    private LobbyType type;
    @OneToOne
    @JoinColumn(nullable = false)
    private AppUser owner;
    @OneToMany(mappedBy = "lobby", orphanRemoval = true)
    private List<Invite> invites;

    public LobbyActive(String title, OffsetTime time, AppUser owner) {
        super(title, time);
        this.owner = owner;
        this.type = LobbyType.ACTIVE;
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
