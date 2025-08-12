package org.lets_play_be.entity.invite;

import jakarta.persistence.*;
import lombok.*;
import org.lets_play_be.entity.enums.InviteState;
import org.lets_play_be.entity.lobby.Lobby;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private AppUser recipient;

    private OffsetDateTime createdAt;

    @Setter
    private boolean isDelivered;

    @Setter
    private boolean isSeen;

    @Enumerated(EnumType.STRING)
    @Setter
    private InviteState state;

    @Setter
    private String message;

    @Setter
    private int delayedFor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lobby_id")
    private Lobby lobby;

    public Invite(AppUser recipient, Lobby lobby, String message) {
        this.recipient = recipient;
        this.state = InviteState.PENDING;
        this.createdAt = OffsetDateTime.now();
        this.isDelivered = false;
        this.isSeen = false;
        this.message = message;
        this.lobby = lobby;
        this.delayedFor = 0;
    }

    public Invite(long id, AppUser recipient, Lobby lobby, String message) {
        this.id = id;
        this.recipient = recipient;
        this.state = InviteState.PENDING;
        this.createdAt = OffsetDateTime.now();
        this.isDelivered = false;
        this.isSeen = false;
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
