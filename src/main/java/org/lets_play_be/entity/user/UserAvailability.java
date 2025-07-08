package org.lets_play_be.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.lets_play_be.entity.enums.AvailabilityEnum;

import java.time.OffsetTime;

@Entity
@Getter
@Table(name = "availability")
@NoArgsConstructor
public class UserAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AvailabilityEnum availabilityType;

    @Setter
    private OffsetTime fromUnavailable;

    @Setter
    private OffsetTime toUnavailable;


    public UserAvailability(AvailabilityEnum availabilityType) {
        this.availabilityType = availabilityType;
        this.fromUnavailable = OffsetTime.parse("00:00:00+00:00");
        this.toUnavailable = OffsetTime.parse("00:00:00+00:00");
    }

    public UserAvailability(long id, AvailabilityEnum availabilityType) {
        this.id = id;
        this.availabilityType = availabilityType;
        this.fromUnavailable = OffsetTime.parse("00:00:00+00:00");
        this.toUnavailable = OffsetTime.parse("00:00:00+00:00");
    }

}
