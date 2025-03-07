package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.*;
import org.lets_play_be.entity.enums.InviteState;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invite {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Setter
    private InviteState state;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_active_id", nullable = false, updatable = false)
    private LobbyActive lobby;

    public Invite(AppUser user, LobbyActive lobby, String message) {
        this.state = InviteState.PENDING;
        this.message = message;
        this.user = user;
        this.lobby = lobby;
    }

}
