package org.lets_play_be.entity.notification;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.lets_play_be.entity.user.AppUser;

import java.time.OffsetDateTime;

@MappedSuperclass
@NoArgsConstructor
@Getter
@ToString
public abstract class Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    AppUser recipient;

    OffsetDateTime createdAt;

    boolean isDelivered;

    boolean isSeen;

    public Notification(AppUser recipient) {
        this.recipient = recipient;
        this.createdAt = OffsetDateTime.now();
        this.isDelivered = false;
        this.isSeen = false;
    }
}