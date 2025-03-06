package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;

import java.time.OffsetTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "lobby_preset")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LobbyPreset extends LobbyBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    AppUser owner;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "accounts_lobby_preset",
            joinColumns = @JoinColumn(name = "lobby_prset_id"),
            inverseJoinColumns = @JoinColumn(name = "accounts_id"))
    List<AppUser> users;


    public LobbyPreset(String title,  OffsetTime time, Long id, AppUser owner, List<AppUser> users) {
        super(title, time);
        setType(LobbyType.PRESET);
        this.id = id;
        this.owner = owner;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        LobbyPreset that = (LobbyPreset) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }


}
