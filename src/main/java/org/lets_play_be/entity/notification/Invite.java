package org.lets_play_be.entity.notification;

import jakarta.persistence.*;
import lombok.*;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.lobby.LobbyActive;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetTime;
import java.util.Objects;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Invite extends Notification {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Setter
    private InviteState state;

    private String message;
    @Setter
    private int delayedFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_active_id", nullable = false, updatable = false)
    private LobbyActive lobby;

    public Invite(AppUser recipient, LobbyActive lobby, String message) {
        super(recipient);
        this.state = InviteState.PENDING;
        this.message = message;
        this.lobby = lobby;
        this.delayedFor = 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Invite that = (Invite) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
