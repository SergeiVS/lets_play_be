package org.lets_play_be.entity.lobby;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lets_play_be.entity.enums.LobbyType;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "lobby_preset")
@NoArgsConstructor
public class LobbyPreset extends LobbyBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "accounts_lobby_preset",
            joinColumns = @JoinColumn(name = "lobby_preset_id"),
            inverseJoinColumns = @JoinColumn(name = "accounts_id"))
    private List<AppUser> users;


    public LobbyPreset(String title, OffsetTime time, AppUser owner, List<AppUser> users) {
        super(title, time, owner);
        setType(LobbyType.PRESET);
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
