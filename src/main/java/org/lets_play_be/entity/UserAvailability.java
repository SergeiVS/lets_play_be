package org.lets_play_be.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.lets_play_be.entity.enums.AvailabilityEnum;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "availability")
@NoArgsConstructor
public class UserAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AvailabilityEnum availabilityType;
    private LocalDateTime fromAvailable;
    private LocalDateTime toAvailable;
    @OneToOne(mappedBy = "availability")
    @JoinColumn(name = "id")
    private AppUser user;

    public UserAvailability(AvailabilityEnum availabilityType) {
        this.availabilityType = availabilityType;
    }

    public UserAvailability(
            AvailabilityEnum availabilityType, LocalDateTime fromAvailable,
            LocalDateTime toAvailable, AppUser user) {
        this.availabilityType = availabilityType;
        this.fromAvailable = fromAvailable;
        this.toAvailable = toAvailable;
        this.user = user;
    }
}
