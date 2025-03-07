package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lets_play_be.entity.enums.LobbyType;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LobbyActive extends LobbyBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private LobbyType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "owner_id")
    private AppUser owner;
    @OneToMany(mappedBy = "lobby", orphanRemoval = true)
    private List<Invite> invites;

    public LobbyActive(String title, OffsetTime time) {
        super(title, time);
        this.type = LobbyType.ACTIVE;
        this.invites = new ArrayList<>();
    }
}
