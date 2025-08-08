package org.lets_play_be.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.type.SqlTypes;
import org.lets_play_be.entity.enums.AvailabilityEnum;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

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
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime unavailableFrom;

    @Setter
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    private OffsetTime unavailableTo;


    public UserAvailability(AvailabilityEnum availabilityType) {
        this.availabilityType = availabilityType;
        this.unavailableFrom = OffsetTime.parse("10:00:00+01:00");
        this.unavailableTo = OffsetTime.parse("11:00:00+01:00");
    }

    public UserAvailability(long id, AvailabilityEnum availabilityType) {
        this.id = id;
        this.availabilityType = availabilityType;
        this.unavailableFrom = OffsetTime.parse("00:00:00+00:00");
        this.unavailableTo = OffsetTime.parse("00:00:00+00:00");
    }

}
